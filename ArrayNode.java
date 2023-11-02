package linkedListArray;

/**
 * @author Ben Kuehner
 *
 * @param <T> - a generic type
 * 
 * 	ArrayNode is used in conjunction with an Array to create a list of nodes. This
 * 	method improves effeciency. 
 * 	Each node contains information of generic type, and an index to the next item
 * 	in the list. 
 */
public class ArrayNode<T> {
		protected int link;
		protected T info;
	  
		public ArrayNode() {
			this.info = null;
		}
	  
		public ArrayNode(T info) {
			this.info = info;
		}
	 
		public void setInfo(T info){ this.info = info;}
		public T getInfo(){ return info; }
		public void setLink(int index){this.link = index;}
		public int getLink(){ return link;}
}
