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

    fun deleteNode(value: T) {
        root = deleteNodeRecursive(root, value)
    }

    private fun deleteNodeRecursive(current: Node<T>?, value: T): Node<T>? {
        if (current == null) {
            return null
        }

        if (value == current.value) {
            // Node to delete found
            if (current.left == null && current.right == null) {
                return null
            }

            if (current.right == null) {
                return current.left
            }

            if (current.left == null) {
                return current.right
            }

            val smallestValue = findSmallestValue(current.right!!)
            current.value = smallestValue.value
            current.right = deleteNodeRecursive(current.right, smallestValue.value)
            return current
        }

        if (value < current.value) {
            current.left = deleteNodeRecursive(current.left, value)
            return current
        }

        current.right = deleteNodeRecursive(current.right, value)
        return current
    }

    private fun findSmallestValue(root: Node<T>): Node<T> {
        return if (root.left == null) {
            root
        } else {
            findSmallestValue(root.left!!)
        }
    }

    //get root node of binary tree as Node.value
    fun getRootNode(): String {
        return root!!.value.toString()
    }

    //get random Node.value from binary tree
    fun getRandomNode(maxIndex: Int): String? {
        val random = Random()
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

        if (maxIndex >= nodes.size) {
            return null
        }

        val randomIndex = random.nextInt(maxIndex)
        return nodes.drop(randomIndex).first().value.toString() + " [" + randomIndex + "]"
    }


    //get Node.value from binary Tree for given index
    fun getNodeByIndex(index: Int): String {
        var randomNode: Node<T>? = null
        var counter = 0
        val iterator = object : Iterator<Node<T>> {
            private var stack = Stack<Node<T>>()

            init {
                pushLeft(root)
            }

            private fun pushLeft(node: Node<T>?) {
                var node = node
                while (node != null) {
                    stack.push(node)
                    node = node.left
                }
            }

            override fun hasNext(): Boolean {
                return !stack.isEmpty()
            }

            override fun next(): Node<T> {
                val node = stack.pop()
                pushLeft(node.right)
                return node
            }
        }
        while (iterator.hasNext()) {
            randomNode = iterator.next()
            if (counter == index) {
                break
            }
            counter++
        }
        return randomNode!!.value.toString()
    }

    //setup binary tree to null
    fun clear() {
        root = null
    }

}
