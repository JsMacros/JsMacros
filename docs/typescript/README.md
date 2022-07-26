# JsMacros-typescript
Type information for the JsMacros minecraft mod

# Setup
## Install the package
Install the version of the package that matches your mod version (for full releases), or 0.0.0-sha (for beta releases)
`$ npm install jsmacros/jsmacros-typescript` (Latest release)  
`$ npm install jsmacros/jsmacros-typescript@beta` (Latest beta)
`$ npm install jsmacros/jsmacros-typescript@1.7.0` (release 1.7.0)  
`$ npm install jsmacros/jsmacros-typescript@0.0.0-30d74d2` (beta-30d74d2)

## Add a tsconfig.json file
### Mandatory fields
These fields tell typescript to use the ambient type definitions without having to import them 

`compilerOptions.typeRoots: ["node_modules"]`  
`compilerOptions.types: ["jsmacros/jsmacros-types"]`

`skipLibCheck: true`: Prevents typescript from reporting errors in the type declaration file - the errors are caused by differences in the typescript and java type systems and are unavoidable

### Example config
```json
{
  "include": ["src"],
  "compilerOptions": {
    "outDir": "dist",
    "typeRoots": ["node_modules"],
    "types": ["jsmacros/jsmacros-types"],
    "skipLibCheck": true
  }
}
```

# Compiling
If you're writing typescript then you need to compile it to javascript before you can use it in the mod.

1. Compile the scripts  
Compile once:  
`$ tsc`  
or compile automatically whenever you make changes to the sources  
`$ tsc --watch`
2. In the mod, select the compiled files from the `outDir` folder instead of the source files
