
// version jsm1.8.4 mc1.19.3
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
/**
 * storing it this way instead of string[] looks better when using it
 * @type {{ [type: string]: never }}
 */
const custom = {}
;[...enumFile.matchAll(/\/\/@[Cc]ustom.*\n *type +(\w+) *= *string/g)]
  .map(m => m[1])
  .forEach(type => custom[type] = null)

//@Unknown
// mark as unknown, don't know how to get the enum (reserved type)
/** @type {string[]} */
const unknown = [...enumFile.matchAll(/\/\/@[Uu]nknown.*\n *type +(\w+) *= *string/g)].map(m => m[1])

//@Enum class.method.method...
// will try to find all static field of that class, and the values are instance of class
// and then call the methods on the values
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
 * for enum, call the methods on value
 * @param {any} value 
 * @param {string[]} methods 
 * @returns 
 */
const call = (value, methods) => {
  for (const method of methods)
    if (!value || (typeof value === 'object' && !(method in value))) return
    else value = value[method]()
  return value
}

/**
 * take the string[] from {@link temp} then place it into file
 * @param {string} type 
 * @returns 
 */
const replaceToFile = type => {
  if (!temp) return
  types[type] = temp.slice()

  let res = ''
  if (!temp.length) {
    res = ' string // not found'
  }else if (type.length + temp.reduce((p, v) => p + v.length + 4, -2) < 80) {
    // single line if short enough
    res = temp.map(f =>    " '" + f.replace(/'/g, "\\'") + "'").join(' |')
  }else {
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

if ('Key' in custom) {
  delete custom.Key
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
}else Chat.log('custom type Key not found')


// get registry classes for evals
const Registry = Java.type('net.minecraft.class_2378')
const Registries = Java.type('net.minecraft.class_7923')
const RegistryKeys = Java.type('net.minecraft.class_7924')
// this is why you need to be in a world
// mc.world.getRegistryManager()
const RegistryManager = Client.getMinecraft().field_1687.method_30349()

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


const customLeft = Object.keys(custom).length
if (customLeft) Chat.log(`There's ${customLeft} not fetched enum:\n${Object.keys(custom).join(', ')}`)
if (unknown[0]) Chat.log(`Enums marked as unknown:\n${unknown.join(', ')}`)

FS.open('./McIdsAndEnums.d.ts').write(enumFile.replace(/\n+$/, "\n"))
log('exported')

function log(msg) {
  Chat.log('[EnumGen] ' + msg)
}
