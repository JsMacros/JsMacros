# JsMacrosAC
The sole purpose of this package is to let your IDE know what functions the various [JsMacros](https://www.curseforge.com/minecraft/mc-mods/jsmacros) classes have.\
Please note that this package does **not** add functionality and will crash your scripts if it is not imported correctly.

# How to import
Each import should be imported with, otherwise your scripts will break. 
```python
if __name__ == "": 
```
If you don't want to import all classes you can import 



aa
a
a
a
a
asadasd

All Classes:
```python
if __name__ == "": from JsMacrosAC import *
```
All Library Classes. These also contain the constants like Chat or GlobalVars.
```python
if __name__ == "": from JsMacrosAC.libraries import *
```
All Event Classes.
```python
if __name__ == "": from JsMacrosAC.events import *
```
All Helper Classes.
```python
if __name__ == "": from JsMacrosAC.helpers import *
```
All Mixin Classes.
```python
if __name__ == "": from JsMacrosAC.mixins import *
```

# In an event file
```python
if __name__ == "": 
    from JsMacrosAC.events import EventAirChange
    event = EventAirChange() #No need to worry about arguments since you do not create the object.
```

# Thing to note
Since Python does not support function overloading, only one function is displayed in some IDEs. In the description of the function you will learn about the different ways to use this function.