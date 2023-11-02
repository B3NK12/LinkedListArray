package linkedListArray;


import java.util.Iterator;

/**
 * @author Benjamin Kuehner
 *
 * @param <T> - any generic type
 * 
 * LinkedListArray is an array of nodes that hold data of generic type, and
 * an integer that represents the index of the next item in the list.
 */
public class LinkedListArray<T> implements ListInterface<T> {
	protected ArrayNode[] elements;   	//array of nodes
	protected final int DEFCAP = 100; 	//default size of array
	protected int origCap;				//capacity of original array
	protected int numElements = 0;   	//number of elements in the list
	protected final int NUL = -1;		//end of array
	protected int first = NUL;			//first usable node
	protected int free = 0;				//first node of free space
	
	//set by find method
	protected boolean found;  			//true if target found
	protected int location;				//index of found item
	protected int previous;				//index of item previous to the found item
	
	/** Default constructor instantiates the array of nodes.
	 *  It sets the origCap which is used in enlarge()
	 *  Each nodes link is set to the following index until the last item which is set to NUL.
	 *  There are two index variables, first represents the first place where a node contains data.
	 *  The nodes link to other indexes where nodes of data exist until a node with a NUL link.
	 *  The free index points to the start of the nodes which do not contain data and can be used
	 *  in conjunction with add to hold useful data. 
	 */
	public LinkedListArray() { 
		origCap = DEFCAP;
		elements = new ArrayNode[DEFCAP];
		for (int i = 0; i < origCap; i++) {
			ArrayNode<T> newNode = new ArrayNode<T>();
			elements[i] = newNode;
			elements[i].setLink(i+1);
		}
		elements[DEFCAP - 1].setLink(NUL);
	}
	
	/** This constructor is similar to the default but the length of the array is integer cap.
	 * @param cap - integer representing the size of the array
	 */
	public LinkedListArray(int cap) {
		origCap = cap;
		elements = new ArrayNode[origCap];
		for (int i = 0; i < origCap; i++) {
			ArrayNode<T> newNode = new ArrayNode<T>();
			elements[i] = newNode;
			elements[i].setLink(i+1);
		}
		elements[origCap-1].setLink(NUL);
	}
	
	/** enlarge() is used in conjunction with add() and getNode()
	 * 	If the array is full, meaning there are no more free nodes, then enlarge() is called
	 * 	before elements are added to the list.
	 */
	private void enlarge() {
		ArrayNode[] larger = new ArrayNode[origCap + elements.length];
		for (int i = 0; i < larger.length; i++) {
			ArrayNode<T> newNode = new ArrayNode<T>(null);
			larger[i] = newNode;
			larger[i].setLink(i+1);
		}
		larger[larger.length-1].setLink(NUL);
		for (int i = 0; i < elements.length; i++) {
			larger[i].setInfo(elements[i].getInfo());
			larger[i].setLink(elements[i].getLink());
		}
		elements = larger;
		free = elements.length-origCap;
	} 
	
	/** This method is used in conjunction with add to grab the next free node. 
	 * If the array is full and no nodes are free then enlarge() is called. 
	 * @return next free node
	 */
	private ArrayNode<T> getNode() {
		if (free == NUL) {
			enlarge();
		}
		return elements[free];
	}
	
	/**	freeNode() is used in conjunction with remove to put node into the free list,
	 * 	and set information to null. The node will become available to add(). The bulk
	 * 	of the remove method is here. 
	 */
	private void freeNode() {
		elements[location].setInfo(null);
		if (location == first) {
			first = elements[location].getLink();
			int temp = free;
			free = location;
			elements[location].setLink(temp);
		} else {
			elements[previous].setLink(elements[location].getLink());
			elements[location].setLink(free);
			free = location;
		}
	}
		
	/** The add method calls getNode() and sets its data to element. Its link is set to the 
	 * index saved in first. First is then set to the index of the new node. Free is also updated.
	 * @param element of generic type to be added
	 * @return true for a successful operation
	 */
	public boolean add(T element) {
		ArrayNode<T> newNode = getNode();
		newNode.setInfo(element);
		int temp = free;
		free = newNode.getLink();
		newNode.setLink(first);
		first = temp;		
		numElements++;
		return true;
	}
	
	/** adds an element to a certain index
	 * 	if the index is a free node, then it checks to see if the node is the first free one. If so,
	 * 	it updates free to the next free node, and updates link and info accordingly.
	 * 	If the free node is not the first one, then the previous free node is found and its link updated.
	 * 	Otherwise, if the node at the index is being used, it is moved elsewhere using add() and the info is updated.
	 */
	@Override
	public void add(int index, T element) {
		if (index < 0 || index>=elements.length) {
			throw new IndexOutOfBoundsException("Invalid index");
		}
		if (numElements == elements.length) {
			enlarge();
		}
		if (elements[index] == null && index == free) {
			elements[index].setInfo(element);
			int temp = elements[index].getLink();
			elements[index].setLink(first);
			first = index;
			free = temp;
		} else if (elements[index] == null && index != free){
			int prev = free;
			for (int i = free; i!=NUL; i=elements[i].getLink()) {
				if (elements[i].getLink() == index) {
					prev = i;
					break;
				}
			}
			elements[index].setInfo(element);
			int temp = elements[index].getLink();
			elements[index].setLink(first);
			first = index;
			elements[prev].setLink(temp);
		} else {
			add((T) elements[index].getInfo());
			elements[index].setInfo(element);
		}
		numElements++;
	}
	
	/**
	 * Sets the data at index to newElement. 
	 * If the node at index is a free node, then add(index, newElement) is called. Null is returned. 
	 * If the node at index is currently being used, then its data is replaced and the old data is returned.
	 */
	@Override
	public T set(int index, T newElement) {
		if (elements[index].getInfo() == null) {
			add(index, newElement);
			return null;
		} else {
			T gone = (T) elements[index].getInfo();
			elements[index].setInfo(newElement);
			return gone;
		}
	}

	/** The find method is used in conjunction with other methods.
	 * 	The method iterates through the nodes containing data, and sets location and previous
	 * 	accordingly if the target element is found. Location and previous are indexes.
	 * @param target - element to find in the array
	 */
	private void find(T target) {		
		previous = first;
		location = first;
		found = false;
		while (location != NUL) {
			if (elements[location].getInfo() != null && elements[location].getInfo().equals(target)) {
				found = true;
				return;
			} else {
				if (location != first) {
					previous++;
				}
				location = elements[location].getLink();
			}
		}
	}
	
	/**
	 *	returns the data stored at an index.
	 */
	@Override
	public T get(int index) {
		if (index < 0 || index>=elements.length) {
			throw new IndexOutOfBoundsException("Invalid index");
		}
		return (T) elements[index].getInfo();
	}
	
	/**
	 * returns the target element if it is found in the list.
	 * Otherwise, it returns null
	 */
	@Override
	public T get(T target) {
		find(target);
		if (found) {
			return target;
		} 
		return null;
	}
	
	/**
	 * returns the index of the target element.
	 * If the element can not be found, then it returns -1
	 */
	@Override
	public int indexOf(T target) {
		find(target);
		if (found) {
			return location;
		} 
		return NUL;
	}

	/**	Contains calls find and returns the found boolean, which is true when the target element
	 * 	is in the array. 
	 * @param target - element being searched for
	 * @return true if the element is found within the array.
	 */
	public boolean contains(T target) {
		find(target);
		return found;
	}

	/**	Remove calls find(), and depending on location, will execute one of three statements.
	 * 	If the element is at index first, then first is set to the following index.
	 * 	If the the element is the last in the list of nodes, then the previous nodes link is set to NUL
	 * 	Otherwise, the previous nodes link is updated to the link strored by our target node.
	 * 	The target nodes link is set to free, and free is updated to the index of the target node.
	 * @param target - element to be removed
	 * @return true if the element is found and subsequentally removed. 
	 */
	public boolean remove(T target) {
		find(target);
		if (found) {
			freeNode();
			numElements--;
			return true;
		} 
		return false;
	}

	/**	This remove method removes an item at an index. It first checks if the node
	 * 	contains any data. If it does, then it searches through and grabs the index
	 *  of the previous node in the list. It then calls on freeNode() to return the
	 *  node to the free list. It also returns the element removed. 
	 */	
	@Override
	public T remove(int index) {
		if (elements[index].getInfo() == null) {
			return null;
		}
		T gone = (T) elements[index].getInfo();
		for (int i=first; i!=NUL; i=elements[i].getLink()) {
			if (elements[i].getLink() == index) {
				previous = i;
				break;
			}
		}
		location = index;
		freeNode();
		numElements--;
		return gone;
	}
	
	/**
	 * @return true if all nodes are being used to contain data
	 */
	public boolean isFull() {
		return (numElements == elements.length);
	}

	/**
	 * @return true if no nodes are being used to contain data.
	 */
	public boolean isEmpty() {
		return numElements == 0;
	}

	/**
	 * @return the number of nodes being used to contain data.
	 */
	public int size() {
		return numElements;
	}

	/** The iterator() returns an Iterator<T> object that traverses the arrayList, returning values,
	 * 	and allowing the removal of values. 
	 * @return an iterator object
	 */
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private int previousPos = NUL;		//index of node prior to current node
			private int currentPos = NUL;		//index of current node
			private int nextPos = first;		//index of next node to return
			
			/** hasNext() returns true if there are more nodes with data, in other words,
			 * 	the current nodes link is not NUL.
			 */
			public boolean hasNext() {
				return (nextPos != NUL);
			}
			
			/** next() throws an exception if there are no more nodes to traverse
			 * 	The method returns data contained by the next node in the list
			 */
			public T next() {
				if (!hasNext()) {
					throw new IndexOutOfBoundsException("Illegal invocation of next");
				}
				T hold = (T) elements[nextPos].getInfo();
				if (currentPos != NUL) {
					previousPos = currentPos;
				}
				currentPos = nextPos;
				nextPos = elements[nextPos].getLink();
				return hold;
			}
			
			/**
			 * Current node gets released and becomes a free node. 
			 * first statment of removal varies depending on the location of the node in the list.
			 */
			public void remove() {
				if (currentPos == first) {
					first = elements[currentPos].getLink();
				} else if (elements[currentPos].getLink() == NUL) {
					elements[previousPos].setLink(NUL);
				} else {
					elements[previousPos].setLink(elements[currentPos].getLink());
				}
				elements[currentPos].setLink(free);
				elements[currentPos].setInfo(null);
				free = currentPos;
				numElements--;
			}
		};
	}
}