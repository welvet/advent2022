package main

import (
	"bufio"
	"os"
	"strconv"
	"strings"
)

type Node struct {
	value int64
	pos   int
	left  *Node
	right *Node
}

func move(n *Node, c int) {
	for c > 0 {
		myOldLeft := n.left

		other := n.right
		otherRight := n.right.right

		n.left = other
		n.right = otherRight

		other.left = myOldLeft
		other.right = n

		myOldLeft.right = other
		otherRight.left = n

		myPos := n.pos
		n.pos = other.pos
		other.pos = myPos

		c--
	}

	for c < 0 {
		myOldRight := n.right

		other := n.left
		otherLeft := n.left.left

		n.left = otherLeft
		n.right = other

		other.left = n
		other.right = myOldRight

		myOldRight.left = other
		otherLeft.right = n

		myPos := n.pos
		n.pos = other.pos
		other.pos = myPos

		c++
	}
}

func findVal(n *Node, v int64) *Node {
	for n.value != v {
		n = n.right
	}

	return n
}

func withOffset(n *Node, offset int) *Node {
	i := 0
	for i < offset {
		n = n.right
		i++
	}

	return n
}

func main() {
	strs := readLines("/Users/alekseik/software/adventofcode2022/data/day20.txt")
	nums := toNums(strs)

	size := len(nums)
	elements := make([]Node, size)
	var prev *Node

	for i, v := range nums {
		elements[i] = Node{v * 811589153, i, nil, nil}

		if prev != nil {
			prev.right = &elements[i]
			(&elements[i]).left = prev
		}
		prev = &elements[i]
	}
	prev.right = &elements[0]
	elements[0].left = prev

	queue := make([]*Node, size*10)
	for i := 0; i < 10; i++ {
		for j, v := range elements {
			queue[j+(i*size)] = &v
		}
	}

	loop := int64((size) * (size - 1))

	for i, v := range queue {
		move(v, int(v.value%loop))
		println("Iteration", i, "of total", len(queue))
	}

	zero := findVal(queue[0], 0)
	var result int64 = 0
	for i := 1000; i <= 3000; i += 1000 {
		value := withOffset(zero, i).value
		println("Value", value)
		result += value
	}

	println("Result", result)
}

func toNums(s []string) []int64 {
	c := make([]int64, len(s))
	for i, v := range s {
		iv, e := strconv.ParseInt(v, 0, 64)
		if e != nil {
			panic(e)
		}

		c[i] = iv
	}

	return c
}

func readLines(path string) []string {
	file, err := os.Open(path)
	if err != nil {
		panic(err)
	}
	defer file.Close()

	var lines []string
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		s := scanner.Text()
		s = strings.TrimSpace(s)

		if len(s) > 0 {
			lines = append(lines, s)
		}
	}

	return lines
}
