# LinkedListArray
An array of nodes that contain data and an index for the next node.

CollectionInterface and ListInterface were created by Dale/Joyce/Weems which I found in "Object-Oriented Data Structures Using Java." 
The ArrayNode is similar to the LLNode explained in the textbook, but the link no longer references a node, but an int representing an index. 
The LinkedListArray is unbounded, and has an enlarge() method which is used in conjunction with add. 
The LinkedListArray implements methods from the interfaces, along with methods ArrayNode getNode() and void freeNode(). 
These array has two index references, first and free. The list contains two lists of sorts, one with nodes containing useful data, and the other contains nodes not being used.
getNode() returns the next free node not being used, enlarging if needed, and freeNode() is used with remove to strip data, and hand over to the free list. 
The LinkedListArray also has an iterator with methods boolean hasNext(), T next(), and T remove(). 
