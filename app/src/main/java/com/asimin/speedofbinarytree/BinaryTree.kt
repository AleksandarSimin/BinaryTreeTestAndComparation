package com.asimin.speedofbinarytree

import java.util.*

class Node<T>(var value: T, var left: Node<T>? = null, var right: Node<T>? = null)

class BinaryTree<T : Comparable<T>> {
    var root: Node<T>? = null

    fun addNode(value: T) {
        root = addRecursive(root, value)
    }

    private fun addRecursive(current: Node<T>?, value: T): Node<T> {
        if (current == null) {
            return Node(value)
        }

        if (value < current.value) {
            current.left = addRecursive(current.left, value)
        } else if (value > current.value) {
            current.right = addRecursive(current.right, value)
        } else {
            // value already exists
            return current
        }

        return current
    }

    fun containsNode(value: T): Boolean {
        return containsNodeRecursive(root, value)
    }

    private fun containsNodeRecursive(current: Node<T>?, value: T): Boolean {
        if (current == null) {
            return false
        }

        if (value == current.value) {
            return true
        }

        return if (value < current.value) {
            containsNodeRecursive(current.left, value)
        } else {
            containsNodeRecursive(current.right, value)
        }
    }

    //get random Node.value from binary tree
    fun getRandomNode(maxIndex: Int): String? {
        val random = Random()                   //random number generator
        val nodes = mutableListOf<Node<T>>()    //list of nodes

        fun traverse(node: Node<T>?) {  //inorder traversal of binary tree
            if (node == null) {         //exit from traverse if node is null
                return
            }
            traverse(node.left)         //traverse left subtree
            nodes.add(node)             //add node to list
            traverse(node.right)        //traverse right subtree
        }

        traverse(root)                  //traverse binary tree

        if (maxIndex >= nodes.size) {   //finish if maxIndex is greater than size of list
            return null
        }

        val randomIndex =
            random.nextInt(maxIndex)                                          //get random index
        return nodes.drop(randomIndex)
            .first().value.toString() + " [" + randomIndex + "]"  //return random node value
    }


    //get Node.value from binary Tree for given index
    fun getNodeByIndex(index: Int): String? {
        val nodes = mutableListOf<Node<T>>()

        fun traverse(node: Node<T>?) {
            if (node == null) {
                return
            }
            traverse(node.left)
            nodes.add(node)
            traverse(node.right)
        }

        traverse(root)

        if (index >= nodes.size) {
            return null
        }

        return nodes.drop(index).first().value.toString() + " [" + index + "]"
    }

    //setup binary tree to null
    fun clear() {
        root = null
    }

}
