
// ~version >=jsm1.8.4 >=mc1.19.3
// need to be in a world to run this script


let enumFile = FS.open('./template.d.ts').read().replace(/\r\n/g, '\n')
// this is here for @Eval or @Custom to access previous fetched types
/** @type {Record<string, string[]>} */
const types = {}

/** @type {Record<string, string>} */
const fromEnum = {}
/** @type {Record<string, string>} */
const fromRegistryHelper = {}
/** @type {Record<string, string>} */
const fromEval = {}

//--- get methods of fetching enums
//@Custom
// will place the code in this script, like Key
/** @type {Set<string>} */
const custom = new Set()
;[...enumFile.matchAll(/\/\/@[Cc]ustom.*\n *type +(\w+) *= *string/g)]
  .map(m => m[1])
  .forEach(type => custom.add(type))

//@Unknown
// mark as unknown, don't know how to get the enum (reserved type)
/** @type {string[]} */
const unknown = [...enumFile.matchAll(/\/\/@[Uu]nknown.*\n *type +(\w+) *= *string/g)].map(m => m[1])

//@Enum class.key.key...
// will try to find all static field of that class, and the values are instance of class
// and then get the fields/call the methods on the values
const EnumMatcher = enumFile.matchAll(/\/\/@Enum +(\S+).*\n *type +(\w+) *= *string/g)
for (const [, expression, type] of EnumMatcher) fromEnum[type] = expression

//@RegistryHelper method
// get ids from `Java.from(Client.getRegistryManager()[method]())`
const RegistryHelperMatcher = enumFile.matchAll(/\/\/@RegistryHelper +(\S+).*\n *type +(\w+) *= *string/g)
for (const [, method, type] of RegistryHelperMatcher) fromRegistryHelper[type] = method

//@Eval code
// eval the code with `eval(code)` to get ids
const EvalMatcher = enumFile.matchAll(/\/\/@Eval +(.*)\n *type +(\w+) *= *string/g)
for (const [, code, type] of EvalMatcher) fromEval[type] = code

/**
 * for enum, get the fields/call the methods on value
 * @param {any} value
 * @param {string[]} keys
 * @returns
 */
function call(value, keys) {
  for (const key of keys)
    if (!value || (typeof value === 'object' && !(key in value))) return
    else {
      value = typeof value[key] === 'function' ? value[key]() : value[key]
    }
  return value
}

/**
 * take the string[] from {@link temp} then place it into file
 * @param {string} type
 * @returns
 */
function replaceToFile(type) {
  if (!temp) return
  types[type] = temp.slice()

  let res = ''
  if (!temp.length) {
    res = ' string // not found'
  } else if (type.length + temp.reduce((p, v) => p + v.length + 4, -2) < 80) {
    // single line if short enough
    res = temp.map(f =>    " '" + f.replace(/'/g, "\\'") + "'").join(' |')
  } else {
    res = temp.map(f => "\n| '" + f.replace(/'/g, "\\'") + "'").join('')
  }

  enumFile = enumFile.replace(
    RegExp(`(\\/\\/@\\w.*\n *type +${type} *=) *string *(?:\n(?=\\|))?`),
    `$1${res}\n`
  )
  temp = undefined
}

log('start')
/** @type {string[]=} */
let temp

log('fetching Enum')
for (const [type, expression] of Object.entries(fromEnum)) {
  const methods = expression.split('.')
  const className = methods.shift()
  const Clazz = Java.type('net.minecraft.' + className)
  temp = Object.values(Clazz)
    .filter(f => f instanceof Clazz)
    .map(f => call(f, methods))
    .sort()
    .filter((v, i, a) => v && v !== a[i - 1])
  log(`fetched ${temp?.length} enums for ${type}`)
  replaceToFile(type)
}


const RegistryHelper = Client.getRegistryManager()

log('fetching from RegistryHelper')
for (const [type, method] of Object.entries(fromRegistryHelper)) {
  temp = Java.from(RegistryHelper[method]())
    .map(id => id.toString())
    .sort()
    .filter((v, i, a) => v && v !== a[i - 1])
  log(`fetched ${temp?.length} ids for ${type}`)
  replaceToFile(type)
}


log('fetching custom')

{
  const ids = Java.from(RegistryHelper.getEntityTypeIds())
    .sort()
    .filter((v, i, a) => v && v !== a[i - 1])
  log(`fetched ${ids.length} ids for EntityId`)
  types['EntityId'] = ids

  const map = ids.map(id => {
    let type = null
    if (id === 'minecraft:player') type = 'PlayerEntityHelper'
    else try {
      type = RegistryHelper.getEntity(id).getClass().getSimpleName()
    } catch {}
    type ||= 'EntityHelper'
    return `\n  '${id}': ${type}`
  }).join('')
  enumFile = enumFile.replace( // type EntityIdToTypeMap = { [id: string]: EntityHelper }
    /type EntityIdToTypeMap = \{ \[id: string\]: EntityHelper \}/,
    `type EntityIdToTypeMap = {${map}\n}\n`
  )
  log('created EntityIdToTypeMap')
  temp = undefined
}

if (custom.has('Key')) {
  custom.delete('Key')
  const InputUtil = Java.type('net.minecraft.class_3675')
  const InputUtil$Type = Java.type('net.minecraft.class_3675$class_307')

  temp = []
  for (const key in InputUtil) // doesn't use Object.keys is because 100 is out of bounds
    if (typeof InputUtil[key] === 'number') temp.push(InputUtil[key])
  temp = temp
    .sort((a, b) => a - b)
    .filter((v, i, a) => v !== a[i - 1])
    // if (code <= 7) InputUtil.Type.MOUSE.createFromCode(code)
    // else InputUtil.Type.KEYSYM.createFromCode(code)
    .map(code => InputUtil$Type[code <= 7 ? 'field_1672' : 'field_1668'].method_1447(code))
    .filter(k => k !== InputUtil.field_16237)
    .map(k => k.method_1441())
    .sort()
  const commonRegex = /^key\.keyboard\.[a-z0-9]$/
  const funcRegex   = /^key\.keyboard\.f([0-9]{1,2})$/
  const groupRegex  = /^key\.keyboard\.(?:keypad|left|right)\./
  temp = temp // order
    .filter(k => funcRegex.test(k))
    .sort((a, b) => a.match(funcRegex)[1] - b.match(funcRegex)[1])
    .concat(temp.filter(k => commonRegex.test(k)))
    .concat(temp.filter(k => groupRegex.test(k)))
    .concat(temp.filter(k => !funcRegex.test(k) && !commonRegex.test(k) && !groupRegex.test(k)))
  log(`fetched ${temp?.length} keys for Key`)
  replaceToFile('Key')
} else log('custom type Key not found')

if (custom.has('ScreenName') || custom.has('ScreenClass')) {
  const HandledScreen = Reflection.getClass('net.minecraft.class_465')

  /** @type {JavaSet<JavaClass>} */
  const screens = GlobalVars.getObject('lastFilteredScreenClasses') ?? (() => {
    /** @type {JavaSet<JavaClass>} */
    const res = JavaUtils.createHashSet()
    const Screen = Reflection.getClass('net.minecraft.class_437')
    const fabricLoader = Client.getMinecraft().getClass().getClassLoader()
    //@ts-ignore
    const loader = Java.type('com.google.common.reflect.ClassPath').from(fabricLoader)
    log('loading through classes to get all screen classes...')
    /** @type {JavaSet} */
    const get = loader.getAllClasses()
    const size = get.size()
    let count = 0
    for (const clz of get) {
      if (++count % 1000 === 0) log(`${count} / ${size}`)
      try {
        const Class = java.lang.Class
        const loaded = Class.forName(clz.getName(), false, fabricLoader)
        // @ts-ignore
        if (Screen.isAssignableFrom(loaded)) res.add(loaded)
      } catch (e) {}
    }
    GlobalVars.putObject('lastFilteredScreenClasses', res)
    return res;
  })()

  if (custom.has('ScreenName')) {
    custom.delete('ScreenName')
    temp = [...screens]
      .filter(c => !c.getTypeName().startsWith('net.minecraft.') && HandledScreen.isAssignableFrom(c))
      .map(c => c.getName())
      .filter(n => n)
      .sort()
      .filter((v, i, a) => v && v !== a[i - 1])
    log(`fetched ${temp?.length} names for ScreenName`)
    replaceToFile('ScreenName')
  } else log('custom type ScreenName not found')

  if (custom.has('ScreenClass')) {
    custom.delete('ScreenClass')
    temp = [...screens]
      .map(c => c.getSimpleName())
      .filter(n => n)
      .sort()
      .filter((v, i, a) => v && v !== a[i - 1])
    log(`fetched ${temp?.length} names for ScreenClass`)
    replaceToFile('ScreenClass')
  } else log('custom type ScreenClass not found')

} else log('custom type ScreenName and ScreenClass not found')


// get registry classes for evals
const Registry = Java.type('net.minecraft.class_2378')
const Registries = Java.type('net.minecraft.class_7923')
const RegistryKeys = Java.type('net.minecraft.class_7924')
const world = Client.getMinecraft().field_1687
// this is why you need to be in a world
// mc.world.getRegistryManager()
const RegistryManager = world.method_30349()

log('fetching from Eval')
for (const [type, code] of Object.entries(fromEval)) {
  temp = eval(code)
  if (!Array.isArray(temp)) temp = Java.from(temp)
  temp = temp
    .sort()
    .filter((v, i, a) => v && v !== a[i - 1])
  log(`fetched ${temp?.length} ids for ${type}`)
  replaceToFile(type)
}


if (custom.size) log(`There's ${custom.size} not fetched enum:\n${[...custom].join(', ')}`)
if (unknown[0]) log(`Enums marked as unknown:\n${unknown.join(', ')}`)

FS.open('./McIdsAndEnums.d.ts').write(enumFile.replace(/\n+$/, "\n"))
log('exported')

function log(msg) {
  Chat.log('[EnumGen] ' + msg)
}
