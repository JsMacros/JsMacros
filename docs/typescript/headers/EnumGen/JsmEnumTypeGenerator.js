
/**
 * version jsm1.8.4 mc1.19.3
 * needed to be in a world to run this script
 * 
 * @typedef {{ [name: string]: string }} Dict
 */

let jsmEnum = FS.open('./JsmEnum-template.d.ts').read().replace(/\r\n/g, '\n')
/** @type {{ [name: string]: string[] }} */
const types = {}
/** @type {Dict} */
const fromEnum = {}
/** @type {Dict} */
const fromRegistryHelper = {}
/** @type {Dict} */
const fromEval = {}
const custom  = jsmEnum.match(/\/\/@[Cc]ustom.*\n *type +\w+ *= *string/g)
  .map(s => s.match(/^\/\/@[Cc]ustom.*\n *type +(\w+) *= *string$/)[1])
const unknown = jsmEnum.match(/\/\/@[Uu]nknown.*\n *type +\w+ *= *string/g)
  .map(s => s.match(/^\/\/@[Uu]nknown.*\n *type +(\w+) *= *string$/)[1])

jsmEnum.match(/\/\/@Enum +\S+.*\n *type +\w+ *= *string/g).forEach(s => {
  const m = s.match(/^\/\/@Enum +(\S+).*\n *type +(\w+) *= *string$/)
  fromEnum[m[2]] = m[1]
})

jsmEnum.match(/\/\/@RegistryHelper +\S+.*\n *type +\w+ *= *string/g).forEach(s => {
  const m = s.match(/^\/\/@RegistryHelper +(\S+).*\n *type +(\w+) *= *string$/)
  fromRegistryHelper[m[2]] = m[1]
})

jsmEnum.match(/\/\/@Eval +.*\n *type +\w+ *= *string/g).forEach(s => {
  const m = s.match(/^\/\/@Eval +(.*)\n *type +(\w+) *= *string$/)
  fromEval[m[2]] = m[1]
})

/** @type {(f: any, methods: string[]) => any} */
const call = (f, methods) => {
  for (const method of methods)
    if (!f || (typeof f === 'object' && !(method in f))) return undefined
    else f = f[method]()
  return f
}

const typeReg = type => RegExp(`(\\/\\/@\\w.*\n *type +${type} *=) *string *(?:\n(?=\\|))?`)
const replaceToFile = type => {
  if (!temp) return
  types[type] = temp.slice()
  let res = ''
  if (!temp.length) res = ' string // not found'
  else if (temp.reduce((p, v) => p + v.length + 4, 0) < 64)
       res = temp.map(f =>    " '" + f.replace(/'/g, "\\'") + "'").join(' |')
  else res = temp.map(f => "\n| '" + f.replace(/'/g, "\\'") + "'").join('')
  jsmEnum = jsmEnum.replace(typeReg(type), `$1${res}\n`)
}

log('start')
let temp
log('fetching Enum')
for (const type in fromEnum) {
  const methods = fromEnum[type].split('.')
  const className = methods.shift()
  const Clazz = Java.type('net.minecraft.' + className)
  temp = Object.values(Clazz)
    .filter(f => f instanceof Clazz)
    .map(f => call(f, methods))
    .sort()
    .filter((v, i, a) => v && v !== a[i - 1])
  replaceToFile(type)
  log(`fetched ${temp?.length} enums for ${type}`)
}

const RegistryHelper = Client.getRegistryManager()

log('fetching from RegistryHelper')
for (const type in fromRegistryHelper) {
  temp = Java.from(RegistryHelper[fromRegistryHelper[type]]())
    .map(id => id.toString())
    .sort()
    .filter((v, i, a) => v && v !== a[i - 1])
  replaceToFile(type)
  log(`fetched ${temp?.length} ids for ${type}`)
}

const Registry = Java.type('net.minecraft.class_2378')
const Registries = Java.type('net.minecraft.class_7923')
const RegistryKeys = Java.type('net.minecraft.class_7924')
const RegistryManager = Client.getMinecraft().field_1687.method_30349()

log('fetching from Eval')
for (const type in fromEval) {
  temp = eval(fromEval[type])
  if (!Array.isArray(temp)) temp = Java.from(temp)
  temp = temp
    .sort()
    .filter((v, i, a) => v && v !== a[i - 1])
  replaceToFile(type)
  log(`fetched ${temp?.length} ids for ${type}`)
}

log('fetching custom')

if (custom.includes('Key')) {
  delete custom[custom.indexOf('Key')]
  const InputUtil = Java.type('net.minecraft.class_3675')
  const InputUtil$Type = Java.type('net.minecraft.class_3675$class_307')

  temp = []
  for (const f in InputUtil)
    if (typeof InputUtil[f] === 'number') temp.push(InputUtil[f])
  temp = temp
    .sort((a, b) => a - b)
    .filter((v, i, a) => v !== a[i - 1])
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
  replaceToFile('Key')
  log(`fetched ${temp?.length} keys for Key`)
}

custom.splice(0, Infinity, ...custom.filter(v => v))
if (custom[0]) Chat.log(`There's ${custom.length} not fetched enum:\n${custom.join(', ')}`)
if (unknown[0]) Chat.log(`enums marked as unknown:\n${unknown.join(', ')}`)

FS.open('./JsmEnum.d.ts').write(jsmEnum.replace(/\n+$/, "\n"))
log('exported')

function log(msg) {
  Chat.log('[JsmEnum] ' + msg)
}
