package edu.brandeis.cs127.pa3;
import java.util.*;
/**
   LeafNodes of B+ trees
 */
public class LeafNode extends Node {

	/**
       Construct a LeafNode object and initialize it with the parameters.
       @param d the degree of the leafnode
       @param k the first key value of the node
       @param n the next node 
       @param p the previous node
	 */
	public LeafNode (int d, int k, Node n, Node p){
		super (d, n, p);
		keys [1] = k;
		lastindex = 1;
	}      


	public void outputForGraphviz() {

		// The name of a node will be its first key value
		// String name = "L" + String.valueOf(keys[1]);
		// name = BTree.nextNodeName();

		// Now, prepare the label string
		String label = "";
		for (int j = 0; j < lastindex; j++) {
			if (j > 0) label += "|";
			label += String.valueOf(keys[j+1]);
		}
		// Write out this node
		BTree.writeOut(myname + " [shape=record, label=\"" + label + "\"];\n");
	}

	/** 
	the minimum number of keys the leafnode should have.
	 */
	public int minkeys () {
		// ADD CODE HERE 
		if (this.getParent() == null)
			return 1;
		else 
			return degree / 2;
	}

	/**
       Check if this node can be combined with other into a new node without splitting.
       Return TRUE if this node and other can be combined. 
       @return true if this node can be combined with other; otherwise false.
	 */
	public boolean combinable (Node other){
		return getLast() + other.getLast() <= maxkeys();
	}

	/**
       Combines contents of this node and its next sibling (nextsib)
       into a single node
	 */
	public void combine (){
		LeafNode nextNode = (LeafNode)(this.getNext());
		int nextIndex = nextNode.getParent().getIndex();
		int nextLast = nextNode.getLast();
		
		System.arraycopy(nextNode.keys, 1, this.keys, this.getLast() + 1, nextLast);
		this.lastindex += nextLast;
		
		LeafNode nextOfNext = (LeafNode)(nextNode.getNext());
		this.setNext(nextOfNext);
		
		if(nextOfNext != null) 
			nextOfNext.setPrev(this);
		
		this.getParent().getNode().delete(nextIndex);	
	}

	/**
       Redistributes keys and pointers in this node and its
       next sibling so that they have the same number of keys
       and pointers, or so that this node has one more key and
       one more pointer.  
       @return int Returns key that must be inserted
       into parent node.
	 */
	public int redistribute (){ 
		LeafNode nextNode = (LeafNode)(this.getNext());
		int nextLast = this.getNext().getLast();
		int[] keyBuffer = new int[this.getLast() + nextLast + 1];
		int newLast = keyBuffer.length / 2;
		
		// no pointers in this version of LeafNode
		System.arraycopy(this.keys, 1, keyBuffer, 1, this.getLast());
		System.arraycopy(nextNode.keys, 1, keyBuffer, this.getLast() + 1, nextLast);
		//Arrays.sort(keyBuffer);
		
		Arrays.fill(this.keys, 0);
		Arrays.fill(nextNode.keys, 0);
		
		System.arraycopy(keyBuffer, 1, this.keys, 1, newLast);
		System.arraycopy(keyBuffer, newLast + 1, nextNode.keys, 1, keyBuffer.length - 1 - newLast);
		
		this.lastindex = newLast;
		nextNode.lastindex = keyBuffer.length - 1 - newLast;
		
		// the new key returned to parent
		int newKey = nextNode.getKey(1);
		if(nextNode.getParent() != null){
			Reference parent = nextNode.getParent();
			int index = parent.getIndex();
			parent.getNode().keys[index] = newKey;
		}
		return newKey;
	}

	/**
       Insert val into this node at keys [i].  (Ignores ptr) Called when this
       node is not full.
       @param val the value to insert to current node
       @param ptr not used now, use null when call this method 
       @param i the index where this value should be
	 */
	public void insertSimple (int val, Node ptr, int i){
		System.arraycopy(this.keys, i, this.keys, i + 1, this.lastindex - i + 1);
		this.keys[i] = val;
		this.lastindex++;
	}


	/**
       Deletes keys [i] and ptrs [i] from this node,
       without performing any combination or redistribution afterwards.
       Does so by shifting all keys from index i+1 on
       one position to the left.  
	 */
	public void deleteSimple (int i){
		// real deleteSimple
		int toDelete = this.getKey(i);
		System.arraycopy(this.keys, i + 1, this.keys, i, this.lastindex - i);
		this.keys[lastindex] = 0;
		this.lastindex--;
		
		// modify keys in internal nodes
		//this.modifyKeyInternal(i);		// why it doesn't work....
		if(i != 1)
			return;
		if(this.getParent() == null || this.getPrev() == null) 
			return; 	
		int index = this.getParent().getIndex();
		if(index != 0){
			// modify the key in parent node
			this.getParent().getNode().keys[index] = this.getKey(1);
			return;
		}
	
		// keep modifying
		InternalNode curr = (InternalNode)(this.getParent().getNode());
		while(curr.getParent() != null){							// while loop until we check the root node
			int currIndex = curr.getParent().getIndex();
			if(toDelete == curr.getParent().getNode().getKey(currIndex)){		//toDelete is the key in parent
				if(this.lastindex == 0)
					curr.getParent().getNode().keys[currIndex] = this.getNext().getKey(1);
				else
					curr.getParent().getNode().keys[currIndex] = this.getKey(1);
			}
			curr = (InternalNode)curr.getParent().getNode();
		}
	} 

	/**
       Uses findKeyIndex, and if val is found, returns the reference with match set to true, otherwise returns
       the reference with match set to false.
       @return a Reference object referring to this node. 
	 */
	
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
	
	public Reference search (int val){
		int index = findKeyIndex(val);
		if(index >= keys.length)
			return new Reference(this, index - 1, false);
		if(keys[index] == val)
			return new Reference(this, index, true);
		else
			return new Reference(this, index, false);
	}

	/**
       Insert val into this, creating split
       and recursive insert into parent if necessary
       Note that ptr is ignored.
       @param val the value to insert
       @param ptr (not used now, use null when calling this method)
	 */
	public void insert (int val, Node ptr){
		int index = this.findKeyIndex(val);
		if(!this.full()){
			if(val > keys[index]) 
				this.insertSimple(val, ptr, this.getLast() + 1);
			else
				this.insertSimple(val, null, index);
			return;
		}
		
		//LeafNode sibling = new LeafNode(degree, val, this.getNext(), this);
		LeafNode sibling = null;
		if (index > this.lastindex) {
			sibling = new LeafNode(degree, val, this.getNext(), this);
		} 
		else {
			sibling = new LeafNode(degree, keys[this.getLast()], this.getNext(), this);	
			this.lastindex--;
			this.insertSimple(val,ptr,index);
		}

		
		int newKey = this.redistribute();
		if(this.getParent() != null){					
			this.getParent().getNode().insert(newKey, sibling);
		}else{											
			// new root
			new InternalNode(degree, this, newKey, sibling, null, null); 
		}
	}
	
	/**
        Print to stdout the content of this node
	 */
	void printNode (){
		System.out.print ("[");
		for (int i = 1; i < lastindex; i++) 
			System.out.print (keys[i]+" ");
		System.out.print (keys[lastindex] + "]");
	}
}

