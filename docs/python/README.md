# JsMacrosAC

The sole purpose of this package is to let your IDE know what functions the
various [JsMacros](https://www.curseforge.com/minecraft/mc-mods/jsmacros) classes have.\
Please note that this package does **not** add functionality and will crash your scripts if it is not imported
correctly.

# How to import

Each import should be imported with leading `if __name__ == "":` otherwise the script will break.
**Import all classes:**

```python
if __name__ == "": from JsMacrosAC import *
```

**Import all classes from a specific module:**

```python
if __name__ == "": from JsMacrosAC.<moduleName> import *
```

**Import one class:**

```python
if __name__ == "": from JsMacrosAC import <ClassName>
```

**Import in an event file**

```python
if __name__ == "": 
    from JsMacrosAC import EventAirChange
    event = EventAirChange() #No need of arguments
```

# Modules

There are some modules in JsMacrosAC that can be imported as well, so you don't have to import all the files.\
These Modules are:
> - libraries (Contains all libraries and constants like Chat or GlobalVars)
> - helpers (Contains all helper classes)
> - events (Contains all event classes)
> - mixins (Contains all mixin classes)
> - rest (Contains all other classes)

#Libraries
The Chat, JavaWrapper, Player, Request, Hud, Time, KeyBind, JsMacros, FS, Reflection, Client, World and GlobalVars
libraries can be accessed by either importing all classes or importing the library module mentioned above.

#Things to note
Since Python does not support function overloading, only one function will be displayed in some IDEs. See the function
description or class to learn more about the different ways to use this function.
