# Folder visualstudiocode
In this folder all directories and files making up a workspace of Visual Studio Code are stored except for the actual source files (HTML, JavaScript, CSS, ...)

#Compile TypeScript files to JavaScript files:

tsc --sourceMap filename.ts


#Automaticall compile JavaScript files:
tsc: watch - tsconfig.json




Companion Concept
=================

When one HTML element has a relation to another HTML Element, for example the thatus of element is displayed in element B.
(Example: The number of words in Element A is counted in Element B and elemet be gets redder tht longer the input is.)
Element A needs a pointer to element B and element B should implemrnt the display update functionality. 
There are several options to implement this:

1) Link dynamically functions and fields
Since alement A  cannot invoke any custom functions on Element B,  Element B would have to store
a pointer to another object that implements the custom functions. So we would need to "inject" an update function into Element b.
elementb["updateFunction"] = () => {...}
Not too elegant, if multiple functions are implemented. There is no type safety anymore. No declarativeness.

2) Wrap element B in a WebComponent
If we use third party HTML Elements, than we need to Wrap the original Element into a new WebComponent. 
This is a possible and not too bad solution.
Disadvantage: If we have a tree of third party elements that interact with each other, wrapping WebComponents around each
singe element will likely disturb the cooperation of the elements in that tree.

3) Linke element B to a companion object.
Preferred solution. We have a perfectly typed object that represents all custom aspects of element b. It is also (to be confirmed)
the factory of element B, especially for linking event handlers to element B if that is necessary.
This way, custom components can be used without modification. Their embedding in Kalisa is done via their companions.
A link from Element A goes to the companion of element B. Full typisation. The companion can provide the original HTML element B
where necessary.