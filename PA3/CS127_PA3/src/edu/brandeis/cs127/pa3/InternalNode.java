package edu.brandeis.cs127.pa3;

import java.util.*;

/**
    Internal Nodes of B+-Trees.
    @author cs127b
 */
public class InternalNode extends Node{

	/**
       Construct an InternalNode object and initialize it with the parameters.
       @param d degree
       @param p0 the pointer at the left of the key
       @param k1 the key value
       @param p1 the pointer at the right of the key
       @param n the next node
       @param p the previous node
	 */

	public InternalNode(int d, Node p0, int k1, Node p1, Node n, Node p) {

		super(d, n, p);
		ptrs[0] = p0;
		keys[1] = k1;
		ptrs[1] = p1;
		lastindex = 1;

		if (p0 != null)
			p0.setParent(new Reference(this, 0, false));
		if (p1 != null)
			p1.setParent(new Reference(this, 1, false));
	}

	/**
	  The minimal number of keys this node should have.
	  
	  @return the minimal number of keys a leaf node should have.
	 */
	public int minkeys() { 
		if (this.getParent() == null) 
			return 1;
		else 
			return (degree + 1) / 2 - 1;
	}

	/**
	  Check if this node can be combined with other into a new node without
	  splitting. Return TRUE if this node and other can be combined.
	 */
	public boolean combinable(Node other) {
		return this.getLast() + other.getLast() + 1 <= this.maxkeys();
	}

	/**
	  Combines contents of this node and its next sibling (next) into a single
	  node,
	 */
	public void combine () {
		if(this.getNext() == null)
			return;
		// bring down the key from parent
		this.lastindex++;
		int nextindex = this.getNext().getParent().getIndex();
		this.keys[lastindex] = this.getParent().getNode().getKey(nextindex);
		
		InternalNode nextNode = (InternalNode)this.getNext();
		int nextLast = nextNode.getLast();
		System.arraycopy(nextNode.keys, 1, this.keys, this.getLast() + 1, nextLast);
		System.arraycopy(nextNode.ptrs, 0, this.ptrs, this.getLast(), nextLast + 1);
		this.lastindex += nextLast;
		
		// reset parent pointers
		this.resetParent();
		
		InternalNode nextOfNext = (InternalNode) nextNode.getNext();
		this.setNext(nextOfNext);
		if(nextOfNext != null)
			nextOfNext.setPrev(this);
		this.getParent().getNode().delete(nextindex); 	// delete the key and the dangling ptr
	}


	/**
	  Redistributes keys and pointers in this node and its next sibling so that
	  they have the same number of keys and pointers, or so that this node has
	  one more key and one more pointer. Returns the key that must be inserted
	  into parent node.
	  
	  @return the value to be inserted to the parent node
	 */
	public int redistribute() {
		if (this.getNext().getParent() == null) {
			// handle case of insertion (just push newKey into parent)
			return this.redistributeInsert((InternalNode)this.getNext(), this.getNext().getLast());		// it is 1...
		} 
		else {
			// handle case of deletion (bring down key in parent!!)
			return this.redistributeDelete((InternalNode)this.getNext(), this.getNext().getLast());
		}
	}
		
	@Override
	public int findKeyIndex (int val){
		int i = 1;
		while (i <= this.lastindex) {
			if(val <= keys[i])
				break;	
			i++;
		}
		return i;
	}
	
	private int redistributeInsert(InternalNode nextNode, int nextLastIndex){
		int[] keyBuffer = new int[this.getLast() + nextLastIndex + 1];
		Node[] ptrBuffer = new Node[this.getLast() + nextLastIndex + 1];
		
		System.arraycopy(this.keys, 1, keyBuffer, 1, this.getLast());
		System.arraycopy(this.ptrs, 0, ptrBuffer, 0, this.getLast() + 1);
		Arrays.fill(this.keys, 0);
		Arrays.fill(this.ptrs, null);
		
		int newKey = keyBuffer[(keyBuffer.length + 1) / 2];
		int nextKey = nextNode.getKey(1);
		Node nextPtr = nextNode.getPtr(1);
		
		int firstLength = (keyBuffer.length + 1) / 2;
		int secondLength = this.getLast() - firstLength;
		
		System.arraycopy(keyBuffer, 1, this.keys, 1, firstLength - 1);
		System.arraycopy(ptrBuffer, 0, this.ptrs, 0, firstLength);
		
		System.arraycopy(keyBuffer, firstLength + 1, nextNode.keys, nextLastIndex, secondLength);
		System.arraycopy(ptrBuffer, firstLength, nextNode.ptrs, 0, secondLength + 1);
	
		this.lastindex = firstLength - 1;
		this.resetParent();
		
		nextNode.lastindex += secondLength;
		nextNode.keys[nextNode.getLast()] = nextKey;
		nextNode.ptrs[nextNode.getLast()] = nextPtr;
		nextNode.resetParent();
		return newKey;
	}
	
	private int redistributeDelete(InternalNode nextNode, int nextLastIndex){
		int[] keyBuffer = new int[this.getLast() + nextLastIndex + 2];
		Node[] ptrBuffer = new Node[this.getLast() + nextLastIndex + 2];
		
		System.arraycopy(this.keys, 1, keyBuffer, 1, this.getLast());
		System.arraycopy(this.ptrs, 0, ptrBuffer, 0, this.getLast() + 1);
		Arrays.fill(this.keys, 0);
		Arrays.fill(this.ptrs, null);
		
		// store the key in parent at nextNode.keys[0]
		int nextIndex = nextNode.getParent().getIndex();
		nextNode.keys[0] = nextNode.getParent().getNode().getKey(nextIndex);
		
		System.arraycopy(nextNode.keys, 0, keyBuffer, this.getLast() + 1, nextLastIndex + 1);
		System.arraycopy(nextNode.ptrs, 0, ptrBuffer, this.getLast() + 1, nextLastIndex + 1);
		Arrays.fill(nextNode.keys, 0);
		Arrays.fill(nextNode.ptrs, null);
		
		int firstLength = (keyBuffer.length + 1) / 2;
		int secondLength = keyBuffer.length - firstLength;
		int newKey = keyBuffer[firstLength];
		this.getParent().getNode().keys[nextIndex] = newKey;
		
		System.arraycopy(keyBuffer, 1, this.keys, 1, firstLength - 1);
		System.arraycopy(ptrBuffer, 0, this.ptrs, 0, firstLength);
		this.lastindex = firstLength - 1;
		this.resetParent();
		
		System.arraycopy(keyBuffer, firstLength + 1, nextNode.keys, 1, secondLength - 1);
		System.arraycopy(ptrBuffer, firstLength, nextNode.ptrs, 0, secondLength);
		nextNode.lastindex = secondLength - 1;
		nextNode.resetParent();
		
		return newKey;
	}
	
	private void resetParent(){
		for(int i = 0; i <= this.getLast(); i++){
			this.ptrs[i].setParent(new Reference(this, i, false));
		}
	}
	
	/**
	  Inserts (val, ptr) pair into this node at keys [i] and ptrs [i]. Called
	  when this node is not full. Differs from {@link LeafNode} routine in that
	  updates parent references of all ptrs from index i+1 on.
	  
	  @param val the value to insert
	  @param ptr the pointer to insert
	  @param i   the position to insert the value and pointer
	 */
	public void insertSimple (int val, Node ptr, int i) {
		System.arraycopy(this.keys, i, this.keys, i + 1, this.getLast() - i + 1);
		System.arraycopy(this.ptrs, i, this.ptrs, i + 1, this.getLast() - i + 1);
		keys[i] = val;
		ptrs[i] = ptr;
		this.lastindex++;
		this.resetParent();
	}
		
	/**
       Deletes keys [i] and ptrs [i] from this node,
       without performing any combination or redistribution afterwards.
       Does so by shifting all keys and pointers from index i+1 on
       one position to the left.  Differs from {@link LeafNode} routine in
       that updates parent references of all ptrs from index i+1 on.
       @param i the index of the key to delete
	 */
	public void deleteSimple (int i) {
		System.arraycopy(this.keys, i + 1, this.keys, i, this.getLast() - i);
		System.arraycopy(this.ptrs, i + 1, this.ptrs, i, this.getLast() - i);
		keys[this.getLast()] = 0;
		ptrs[this.getLast()] = null;
		this.lastindex--;
		this.resetParent();
	}
		
	/**
       Uses findPtrInex and calls itself recursively until find the value or find the position 
       where the value should be.
       @return the referenene pointing to a leaf node.
	 */
	public Reference search (int val) {
		int index = this.findPtrIndex(val);
		if(ptrs[index] == null)
			return null;
		return ptrs[index].search(val);
	}
		
	/**
	   Insert (val, ptr) into this node. Uses insertSimple, redistribute etc.
	   Insert into parent recursively if necessary
	   @param val the value to insert
	   @param ptr the pointer to insert 
	 */
	public void insert (int val, Node ptr) {
		int index = this.findKeyIndex(val);
		if(!this.full()){
			this.insertSimple(val, ptr, index);
			return;
		}
	
		InternalNode sibling = null;
		if (index > this.lastindex) {
			sibling = new InternalNode(degree, null, val, ptr, this.getNext(), this);
		} 
		else {
			sibling = new InternalNode(degree, null, keys[this.getLast()], ptrs[this.getLast()], this.getNext(), this);	
			this.lastindex--;
			this.insertSimple(val,ptr,index);
		}
			
		//InternalNode sibling = new InternalNode(degree, null, val, ptr, this.getNext(), this);
		int newKey = this.redistribute();		
		//int newKey = this.redistributeInsert(sibling, sibling.getLast());
		if (this.getParent() != null) {
			// recursive call
			this.getParent().getNode().insert(newKey, sibling);
		} 
		else {
			// new root
			new InternalNode(degree, this, newKey, sibling, null, null);
		}
	}
	

	public void outputForGraphviz() {

		// The name of a node will be its first key value
		// String name = "I" + String.valueOf(keys[1]);
		// name = BTree.nextNodeName();

		// Now, prepare the label string
		String label = "";
		for (int j = 0; j <= lastindex; j++) {
			if (j > 0)
				label += "|";
			label += "<p" + ptrs[j].myname + ">";
			if (j != lastindex)
				label += "|" + String.valueOf(keys[j + 1]);
			// Write out any link now
			BTree.writeOut(myname + ":p" + ptrs[j].myname + " -> " + ptrs[j].myname + "\n");
			// Tell your child to output itself
			ptrs[j].outputForGraphviz();
		}
		// Write out this node
		BTree.writeOut(myname + " [shape=record, label=\"" + label + "\"];\n");
	}

	/**
	 * Print out the content of this node
	 */
	void printNode() {

		int j;
		System.out.print("[");
		for (j = 0; j <= lastindex; j++) {

			if (j == 0)
				System.out.print(" * ");
			else
				System.out.print(keys[j] + " * ");

			if (j == lastindex)
				System.out.print("]");
		}
	}
}