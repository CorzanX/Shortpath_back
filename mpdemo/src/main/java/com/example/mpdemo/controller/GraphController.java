package com.example.mpdemo.controller;

import com.example.mpdemo.entity.*;
import com.example.mpdemo.mapper.ExerciseMapper;
import com.example.mpdemo.mapper.LiteratureMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin

public class GraphController {
    @Autowired
    private ExerciseMapper exerciseMapper;
    @Autowired
    private LiteratureMapper literatureMapper;
    private GraphData graph_upload;

    private GraphData graph_example;
    private GraphData graph_dense;
    private GraphData graph_sparse;
    private GraphData graph_negative;



    @PostConstruct
    public void initData(){
        this.graph_dense = init_graph("Dense.txt");
        this.graph_example = init_graph("random.txt");
        this.graph_sparse = init_graph("sparse.txt");
        this.graph_negative = init_graph("negative.txt");
    }
    @GetMapping("/exercise")
    public List<Exercise> find_exercise(){return exerciseMapper.selectAllExercise();}

    @GetMapping("/literature")
    public List<Literature> find_literature(){return literatureMapper.selectAllLiterature();}

    @PostMapping("/cmp")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            List<node> nodes = new ArrayList<>();
            List<link> links = new ArrayList<>();
            String line;
            int flag = -1;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if(parts[0].equals("directed"))flag = 0;
                else if(parts[0].equals("undirected")) flag = 1;
                if (parts[0].equals("vertex")) {
                    node node = new node(nodes.size(), parts[1]);
                    nodes.add(node);
                } else if (parts[0].equals("edge")) {
                    String sourceName = parts[1];
                    String targetName = parts[2];
                    int weight = Integer.parseInt(parts[3]);
                    int sourceIndex = -1;
                    int targetIndex = -1;
                    for (int i = 0; i < nodes.size(); i++) {
                        node tempNode = nodes.get(i);
                        if (tempNode.getName().equals(sourceName)) {
                            sourceIndex = tempNode.getId();
                        }
                        if (tempNode.getName().equals(targetName)) {
                            targetIndex = tempNode.getId();
                        }
                    }
                    link link = new link(sourceIndex, targetIndex, weight);
                    links.add(link);
                    if(flag == 1) link = new link(targetIndex, sourceIndex, weight);
                    links.add(link);
                }
            }
            ObjectMapper objectMapper = new ObjectMapper();
            graph_upload = new GraphData(nodes, links,flag);
            String graphDataJson = objectMapper.writeValueAsString(graph_upload);

            return ResponseEntity.ok().body(graphDataJson);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/getstp")
    public String info(@RequestPart("input_start") String start,
                         @RequestPart("input_end") String end){
        graph_upload.findShortestPath_dij(start,end);
        graph_upload.findShortestPath_floyd(start,end);
        graph_upload.findShortestPath_spfa(start,end);
        graph_upload.findShortestPath_bellmanFord(start,end);
        if(graph_upload.findShortestPath_dij(start, end).equals("false"))
            return "There's no way from " + start + "to " + end;
        else
            return "The shortest path from " + start + " to " + end + " is: " +
                    graph_upload.findShortestPath_dij(start, end) + "\n"
                    + gettime(start,end,graph_upload) + "\n";

    }

    @GetMapping("/cmp_random")
    public ResponseEntity<String> handleLocalFile_random() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String graphDataJson = objectMapper.writeValueAsString(graph_example);

            return ResponseEntity.ok().body(graphDataJson);
        }catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("cmp_random_times")
    public String info_random(){
        String start = "seattle",end = "boston";
        graph_example.findShortestPath_dij(start,end);
        graph_example.findShortestPath_floyd(start,end);
        graph_example.findShortestPath_spfa(start,end);
        graph_example.findShortestPath_bellmanFord(start,end);
        return gettime(start,end,graph_example) + "\n";
    }


    @GetMapping("/cmp_dense")
    public ResponseEntity<String> handleLocalFile_dense() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String graphDataJson = objectMapper.writeValueAsString(graph_dense);

            return ResponseEntity.ok().body(graphDataJson);
        }catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("cmp_dense_times")
    public String info_dense(){
        String start = "A",end = "E";
        graph_dense.findShortestPath_dij(start,end);
        graph_dense.findShortestPath_floyd(start,end);
        graph_dense.findShortestPath_spfa(start,end);
        graph_dense.findShortestPath_bellmanFord(start,end);
        return  gettime(start,end,graph_dense) + "\n";
    }

    @GetMapping("/cmp_sparse")
    public ResponseEntity<String> handleLocalFile_sparse() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String graphDataJson = objectMapper.writeValueAsString(graph_sparse);

            return ResponseEntity.ok().body(graphDataJson);
        }catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("cmp_sparse_times")
    public String info_sparse(){
        String start = "A",end = "E";
        graph_sparse.findShortestPath_dij(start,end);
        graph_sparse.findShortestPath_floyd(start,end);
        graph_sparse.findShortestPath_spfa(start,end);
        graph_sparse.findShortestPath_bellmanFord(start,end);
        return gettime(start,end,graph_sparse) + "\n";
    }

    @GetMapping("/cmp_negative")
    public ResponseEntity<String> handleLocalFile_negative() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String graphDataJson = objectMapper.writeValueAsString(graph_negative);

            return ResponseEntity.ok().body(graphDataJson);
        }catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("cmp_negative_times")
    public String info_negative(){
        String start = "seattle",end = "boston";

        //graph_negative.findShortestPath_spfa(start,end);
        graph_negative.findShortestPath_bellmanFord(start,end);
        return  "Dijkstra : ∞ nanoseconds."+
                "\n" + "Floyd : ∞ nanoseconds." +
                "\n" + "SPFA : ∞ nanoseconds."+
                "\n" + bm_time(start,end,graph_negative);
    }

    public GraphData init_graph(String f){
        GraphData g;
        try {
            ClassPathResource resource = new ClassPathResource("/static/"+f);
            InputStream inputStream = resource.getInputStream();
            //File file = new File("src/main/resources/static/"+f);
            //InputStream inputStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            List<node> nodes = new ArrayList<>();
            List<link> links = new ArrayList<>();
            String line;
            int flag = -1;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if(parts[0].equals("directed")) flag = 0;
                else if(parts[0].equals("undirected")) flag = 1;
                if (parts[0].equals("vertex")) {
                    node node = new node(nodes.size(), parts[1]);
                    nodes.add(node);
                } else if (parts[0].equals("edge")) {
                    String sourceName = parts[1];
                    String targetName = parts[2];
                    int weight = Integer.parseInt(parts[3]);
                    int sourceIndex = -1;
                    int targetIndex = -1;
                    for (int i = 0; i < nodes.size(); i++) {
                        node tempNode = nodes.get(i);
                        if (tempNode.getName().equals(sourceName)) {
                            sourceIndex = tempNode.getId();
                        }
                        if (tempNode.getName().equals(targetName)) {
                            targetIndex = tempNode.getId();
                        }
                    }
                    link link = new link(sourceIndex, targetIndex, weight);
                    links.add(link);
                    if(flag == 1) link = new link(targetIndex, sourceIndex, weight);
                    links.add(link);
                }
            }

            g = new GraphData(nodes, links, flag);
            return g;
        } catch (IOException e) {
            System.out.println("no such file");
            return null;
        }
    }
    public String gettime(String start,String end,GraphData g){
        return dij_time(start,end,g)+ "\n" + floyd_time(start,end,g) + "\n" + spfa_time(start,end,g) + "\n" + bm_time(start,end,g);
    }

    public String bm_time(String start,String end,GraphData g){
        long startTime = System.nanoTime();
        g.findShortestPath_bellmanFord(start,end);
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        return "Bellman-Ford : " + executionTime +" nanoseconds.";
    }

    public String floyd_time(String start,String end,GraphData g){
        long startTime = System.nanoTime();
        g.findShortestPath_floyd(start,end);
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        return "Floyd : " + executionTime +" nanoseconds.";
    }
    public String spfa_time(String start,String end,GraphData g){
        long startTime = System.nanoTime();
        g.findShortestPath_spfa(start,end);
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        return "SPFA : " + executionTime +" nanoseconds.";
    }
    public String dij_time(String start,String end,GraphData g){
        long startTime = System.nanoTime();
        g.findShortestPath_dij(start,end);
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        return "Dijkstra : " + executionTime +" nanoseconds.";
    }

}
