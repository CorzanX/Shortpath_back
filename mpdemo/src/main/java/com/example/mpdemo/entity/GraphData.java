package com.example.mpdemo.entity;

import java.util.*;
import java.util.List;
import java.lang.Math;
public class GraphData {
    private List<node> nodes;
    private List<link> links;






    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    private int flag;
    public GraphData(List<node> nodes, List<link> links,int flag) {
        this.nodes = nodes;
        this.links = links;
        this.flag = flag;
    }

    public List<node> getNodes() {
        return nodes;
    }

    public void setNodes(List<node> nodes) {
        this.nodes = nodes;
    }

    public List<link> getLinks() {
        return links;
    }

    public void setLinks(List<link> links) {
        this.links = links;
    }

    public String findShortestPath_dij(String start, String end) {
        // 创建节点映射表，用于快速查找节点
        Map<Integer, node> nodeMap = new HashMap<>();
        int start_int=0,end_int=0;
        for (node n : nodes) {
            nodeMap.put(n.getId(), n);
            if(n.getName().equals(start))start_int=n.getId();
            if(n.getName().equals(end))end_int=n.getId();
        }

        // 创建连接关系映射表
        Map<node, List<link>> graph = new HashMap<>();
        for (link l : links) {
            node sourceNode = nodeMap.get(l.getSource());
            List<link> adjacentLinks = graph.getOrDefault(sourceNode, new ArrayList<>());
            adjacentLinks.add(l);
            graph.put(sourceNode, adjacentLinks);
        }

        // 创建距离和前驱节点映射表
        Map<node, Integer> distance = new HashMap<>();
        Map<node, node> previous = new HashMap<>();
        for (node n : nodes) {
            distance.put(n, Integer.MAX_VALUE);
            previous.put(n, null);
        }

        // 使用优先队列保存待访问的节点，根据距离排序
        PriorityQueue<node> queue = new PriorityQueue<>(Comparator.comparingInt(distance::get));
        node startNode = nodeMap.get(start_int);
        distance.put(startNode, 0);
        queue.offer(startNode);

        while (!queue.isEmpty()) {
            node current = queue.poll();

            // 如果当前节点已经是终点，则停止搜索
            if (current.getName().equals(end_int)) {
                break;
            }

            int currentDistance = distance.get(current);

            // 获取当前节点的相邻连接
            List<link> adjacentLinks = graph.get(current);
            if (adjacentLinks != null) {
                for (link l : adjacentLinks) {
                    node nextNode = nodeMap.get(l.getTarget());
                    int weight = l.getWeight();
                    int newDistance = currentDistance + weight;

                    // 如果找到了更短的路径，则更新距离和前驱节点
                    if (newDistance < distance.get(nextNode)) {
                        distance.put(nextNode, newDistance);
                        previous.put(nextNode, current);
                        queue.offer(nextNode);
                    }
                }
            }
        }

        // 根据前驱节点映射表构建最短路径
        List<String> path = new ArrayList<>();
        node currentNode = nodeMap.get(end_int);
        if (distance.get(currentNode) == Integer.MAX_VALUE) {
            // Destination node is unreachable
            return "false";
        }
        while (currentNode != null) {
            path.add(0, currentNode.getName());
            currentNode = previous.get(currentNode);
        }

        // 将路径转换为字符串
        StringBuilder pathString = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            pathString.append(path.get(i));
            if (i < path.size() - 1) {
                pathString.append("->");
            }
        }
        if (path.size() > 0 && !path.get(0).equals(start)) {
            pathString.insert(0, start + "->");
        }

        return pathString.toString();
    }
    public String findShortestPath_floyd(String start, String end) {
        int n = nodes.size(); // 节点数量

        // 构建距离矩阵和路径矩阵
        int[][] distance = new int[n][n];
        int[][] path = new int[n][n];

        // 初始化距离矩阵和路径矩阵
        for (int i = 0; i < n; i++) {
            Arrays.fill(distance[i], Integer.MAX_VALUE);
            Arrays.fill(path[i], -1);
        }

        // 初始化距离矩阵和路径矩阵（根据连接关系）
        for (link l : links) {
            int sourceIndex = l.getSource();
            int targetIndex = l.getTarget();
            int weight = l.getWeight();
            distance[sourceIndex][targetIndex] = weight;
            path[sourceIndex][targetIndex] = targetIndex;
        }

        // Floyd 算法核心步骤
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (distance[i][k] != Integer.MAX_VALUE && distance[k][j] != Integer.MAX_VALUE) {
                        int throughK = distance[i][k] + distance[k][j];
                        if (throughK < distance[i][j]) {
                            distance[i][j] = throughK;
                            path[i][j] = path[i][k];
                        }
                    }

                }
            }
        }

        // 根据路径矩阵构建最短路径
        List<String> pathList = new ArrayList<>();
        int startIndex = getNodeIndexByName(start);
        int endIndex = getNodeIndexByName(end);
        if (path[startIndex][endIndex] == -1) {
            return "No path found";
        }
        pathList.add(nodes.get(startIndex).getName());
        while (startIndex != endIndex) {
            startIndex = path[startIndex][endIndex];
            pathList.add(nodes.get(startIndex).getName());
        }

        // 将路径转换为字符串
        StringBuilder pathString = new StringBuilder();
        for (int i = 0; i < pathList.size(); i++) {
            pathString.append(pathList.get(i));
            if (i < pathList.size() - 1) {
                pathString.append("->");
            }
        }

        return pathString.toString();
    }

    private int getNodeIndexByName(String name) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public String findShortestPath_spfa(String start, String end) {
        Map<Integer, node> nodeMap = new HashMap<>();
        int start_int = 0, end_int = 0;
        for (node n : nodes) {
            nodeMap.put(n.getId(), n);
            if (n.getName().equals(start)) start_int = n.getId();
            if (n.getName().equals(end)) end_int = n.getId();
        }

        int n = nodes.size(); // 节点数量
        int[] distance = new int[n];
        int[] previous = new int[n];
        boolean[] inQueue = new boolean[n];
        Arrays.fill(distance, Integer.MAX_VALUE);
        Arrays.fill(previous, -1);

        Queue<Integer> queue = new LinkedList<>();
        queue.offer(start_int);
        distance[start_int] = 0;
        inQueue[start_int] = true;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            inQueue[current] = false;

            List<link> adjacentLinks = getAdjacentLinks(current, nodeMap);
            if (adjacentLinks != null) {
                for (link l : adjacentLinks) {
                    int nextNodeIndex = l.getTarget();
                    int weight = l.getWeight();
                    int newDistance = distance[current] + weight;

                    if (newDistance < distance[nextNodeIndex]) {
                        distance[nextNodeIndex] = newDistance;
                        previous[nextNodeIndex] = current;
                        if (!inQueue[nextNodeIndex]) {
                            queue.offer(nextNodeIndex);
                            inQueue[nextNodeIndex] = true;
                        }
                    }
                }
            }
        }

        List<String> path = new ArrayList<>();
        int currentNode = end_int;
        while (currentNode != -1) {
            path.add(0, nodeMap.get(currentNode).getName());
            currentNode = previous[currentNode];
        }

        StringBuilder pathString = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            pathString.append(path.get(i));
            if (i < path.size() - 1) {
                pathString.append("->");
            }
        }

        return pathString.toString();
    }

    public String findShortestPath_bellmanFord(String start, String end) {
        Map<Integer, node> nodeMap = new HashMap<>();
        int start_int = 0, end_int = 0;
        for (node n : nodes) {
            nodeMap.put(n.getId(), n);
            if (n.getName().equals(start)) start_int = n.getId();
            if (n.getName().equals(end)) end_int = n.getId();
        }

        int n = nodes.size(); // 节点数量
        int[] distance = new int[n];
        int[] previous = new int[n];
        Arrays.fill(distance, Integer.MAX_VALUE);
        Arrays.fill(previous, -1);

        distance[start_int] = 0;

        for (int i = 1; i < n; i++) {
            for (link l : links) {
                int sourceNodeIndex = l.getSource();
                int targetNodeIndex = l.getTarget();
                int weight = l.getWeight();

                if (distance[sourceNodeIndex] != Integer.MAX_VALUE && distance[sourceNodeIndex] + weight < distance[targetNodeIndex]) {
                    distance[targetNodeIndex] = distance[sourceNodeIndex] + weight;
                    previous[targetNodeIndex] = sourceNodeIndex;
                }
            }
        }

        for (link l : links) {
            int sourceNodeIndex = l.getSource();
            int targetNodeIndex = l.getTarget();
            int weight = l.getWeight();
            if (distance[sourceNodeIndex] != Integer.MAX_VALUE && distance[sourceNodeIndex] + weight < distance[targetNodeIndex]) {
                // 存在负权回路
                return "Negative-weight cycle detected. No shortest path exists.";
            }
        }

        List<String> path = new ArrayList<>();
        int currentNode = end_int;
        while (currentNode != -1) {
            path.add(0, nodeMap.get(currentNode).getName());
            currentNode = previous[currentNode];
        }

        StringBuilder pathString = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            pathString.append(path.get(i));
            if (i < path.size() - 1) {
                pathString.append("->");
            }
        }

        return pathString.toString();
    }

    private List<link> getAdjacentLinks(int nodeId, Map<Integer, node> nodeMap) {
        node currentNode = nodeMap.get(nodeId);
        List<link> adjacentLinks = new ArrayList<>();
        for (link l : links) {
            if (l.getSource() == currentNode.getId()) {
                adjacentLinks.add(l);
            }
        }
        return adjacentLinks;
    }



    public String getAdjacencyMatrixString() {
        int size = nodes.size();
        int[][] adjacencyMatrix = new int[size][size];

        // 初始化邻接矩阵
        for (link l : links) {
            int source = l.getSource();
            int target = l.getTarget();
            adjacencyMatrix[source][target] = l.getWeight();

        }

        // 构建邻接矩阵字符串
        StringBuilder sb = new StringBuilder();
        sb.append("    ");
        for (node n : nodes) {
            sb.append(String.format("%-15s", n.getName()));
        }
        sb.append("\n");

        for (int i = 0; i < size; i++) {
            sb.append(nodes.get(i).getName()).append("   ");
            for (int j = 0; j < size; j++) {
                sb.append(String.format("%-15d", adjacencyMatrix[i][j]));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "GraphData{" +
                "nodes=" + nodes +
                ", links=" + links +
                '}';
    }



}


