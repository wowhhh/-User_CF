package com.wyb.rec.CollaborationFiltering;
/**
 * Ê÷½áµã
 * @author wyb
 *
 */
public class TreeNode {
	public double val;
	int id;
	TreeNode left;
	TreeNode right;
	
	TreeNode(){}
	
	public TreeNode(int id,Double similarity){
		this.id=id;
		this.val=similarity;
	}
}
