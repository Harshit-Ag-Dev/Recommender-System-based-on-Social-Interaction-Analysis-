package com.mycompany.recommenderengine;

import java.util.*;

class Recommender {
    ArrayList<Integer> source = new ArrayList<>();
    ArrayList<Integer> target = new ArrayList<>();
    ArrayList<Integer> weights = new ArrayList<>();
    int[] inDegree = new int[50];
    int[] outDegree = new int[50];
    ArrayList<Integer> originalUsers = new ArrayList<>();
    ArrayList<Integer> terminalUsers = new ArrayList<>();
    ArrayList<Integer> notUsers = new ArrayList<>();
    ArrayList<Integer> allUsers = new ArrayList<>();
    ArrayList<ArrayList<List<Integer>>> completePathSet = new ArrayList<>();
    ArrayList<List<Integer>> completePathSetFotTargetUser = new ArrayList<>();
    ArrayList<List<Integer>> cp = new ArrayList<>();
    ArrayList<Double> sstList = new ArrayList<>();
    ArrayList<Integer> userList = new ArrayList<>();
    ArrayList<Double> ratesList = new ArrayList<>();
    ArrayList<Double> srpScore = new ArrayList<>();
    double temp1 = 0;
    int temp2 = 0;
    HashMap<Double,Integer> recommmendations = new HashMap<>();
    TreeMap<Double,Integer> finalOutput = new TreeMap<>();
    
    // function to generate the nodes and edges of the network

    public void generateNetwork() {
        Random rand = new Random();
        
        // generating nodes and edges for the network
        for (int i=0; i<100; i++) {
            source.add(rand.nextInt(50));
            target.add(rand.nextInt(50));
        }

        // checking for duplicates
        for (int i=0; i<source.size(); i++) {
            if (source.get(i) == target.get(i)) {
                source.remove(i);
                target.remove(i);
            }
            for (int j=i+1; j<source.size(); j++) {
                if (source.get(i) == source.get(j) && target.get(i) == target.get(j)) {
                    source.remove(j);
                    target.remove(j);
                }
            }
        }
        for (int i=0; i<source.size(); i++)
            weights.add(rand.nextInt(5)+1);
    }


    // function to get the original and terminal users 
    // original users -> in degree = 0
    // terminal users -> out degree = 0
    public void getUsers() {
        for (int i=0; i<50; i++) {
            inDegree[i] = 0;
            outDegree[i] = 0;
        }
        for (int i=0; i<source.size(); i++) {
            outDegree[source.get(i)] += 1;
            inDegree[target.get(i)] += 1; 
        }
        for (int i=0; i<50; i++) {
            if (inDegree[i] == 0 && outDegree[i] == 0)
                notUsers.add(i);
            else if (inDegree[i] == 0 && outDegree[i]!=0)
                originalUsers.add(i);
            else if (outDegree[i] == 0 && inDegree[i]!=0)
                terminalUsers.add(i);
        }
        for (int i=0; i<50; i++) {
            allUsers.add(i);
        }
        for (int i=0; i<notUsers.size(); i++) {
            allUsers.remove(notUsers.get(i));
        }
    }


    public void getAllCompletePaths() {
        Graph g = new Graph(source.size());
        for (int i=0; i<source.size(); i++)
            g.addEdge(source.get(i), target.get(i));
        for (int i=0; i<originalUsers.size(); i++) {
            for (int j=0; j<terminalUsers.size(); j++) {
                g.printAllPaths(source.get(i), target.get(j));
                completePathSet.add(g.completePathList);
            }
        }
        System.out.println(completePathSet.size());
    }



    public void getCompletePathSetForTargetUser(int user) {
        completePathSetFotTargetUser = new ArrayList<>();
        for (int i=0; i<completePathSet.size(); i++) {
            for (int j=0; j<completePathSet.get(i).size(); j++) {
                if (completePathSet.get(i).get(j).indexOf(user) != -1)
                    completePathSetFotTargetUser.add(completePathSet.get(i).get(j));
            }
        }
    }


    public void gettingSST(int user) {
        ratesList = new ArrayList<>();
        sstList = new ArrayList<>();
        userList = new ArrayList<>();
        int rateLocal = 0;
        cp = completePathSetFotTargetUser;
        for (int i:allUsers) {
            int r=0, sd=0, counter=0, sdGlobal=0;
            double sstLocal=0; 
            if (i == user)
                continue;
            for (int j=0; j<cp.size(); j++) {
                if ((cp.get(j).size()!=0) && (cp.get(j).indexOf(i)!=-1)) {
                    counter += 1;
                    sd = Math.abs(cp.get(j).indexOf(i) - cp.get(j).indexOf(user));
                    sdGlobal += sd;
                    if (cp.get(j).indexOf(i) > cp.get(j).indexOf(user)) {
                        for (int k=cp.get(j).indexOf(user); k<cp.get(j).indexOf(i); k++) {
                            for (int z=0; z<source.size(); z++) {
                                if ((source.get(z) == cp.get(j).get(k)) && (target.get(z) == cp.get(j).get(k+1)))
                                    rateLocal = weights.get(z);
                            }
                            r += rateLocal;
                            sstLocal = (double)rateLocal/(1+sd);
                        }
                    }
                    else {
                        for (int k=cp.get(j).indexOf(i); k<cp.get(j).indexOf(user); k++) {
                            for (int z=0; z<source.size(); z++) {
                                if ((source.get(z) == cp.get(j).get(k)) && (target.get(z) == cp.get(j).get(k+1)))
                                    rateLocal = weights.get(z);
                            }
                            r += rateLocal;
                            sstLocal = (double)rateLocal/(1+sd);
                        }
                    }
                }
            }
            if (counter != 0) {
                ratesList.add((double)r/sdGlobal);
                sstList.add((double)sstLocal/counter);
                userList.add(i);
            }
        }
    }

    public void generateSRP(int user) {
        for (int i=0; i<sstList.size(); i++) 
            srpScore.add((double)(sstList.get(i) * Math.abs(sstList.get(i) - ratesList.get(i)))*1000);
        for (int i=0; i<sstList.size(); i++) {
            recommmendations.put(srpScore.get(i),userList.get(i));
        }
        finalOutput.putAll(recommmendations);
    }

    // public void finalOutput() {
    //     for (int i=0; i<srpScore.size()-1; i++) {
    //         for (int j=i+1; j<srpScore.size(); j++) {
    //             if (srpScore.get(i) > srpScore.get(j)) {
    //                 temp1 = srpScore.get(i);
    //                 srpScore.remove(i);
    //                 srpScore.add(i,srpScore.get(j));
    //                 srpScore.remove(j);
    //                 srpScore.add(j,temp1);

    //                 temp2 = userList.get(i);
    //                 userList.remove(i);
    //                 userList.add(i,userList.get(j));
    //                 userList.remove(j);
    //                 userList.add(j,temp2);
    //             }
    //         }
    //     }
    // }
    

    // Driver Method
    public static void main(String[] args) {
        Recommender ob = new Recommender();
        ob.generateNetwork();
        ob.getUsers();
        // System.out.println(ob.source);
        // System.out.println(ob.target);
        // System.out.println(ob.weights);
        // System.out.println(ob.originalUsers);
        // System.out.println(ob.terminalUsers);
        // System.out.println(ob.notUsers);
        System.out.println(ob.allUsers);
        ob.getAllCompletePaths();
        ob.getCompletePathSetForTargetUser(35);
        //System.out.println(ob.completePathSetFotTargetUser.size());
        ob.gettingSST(35);
        ob.generateSRP(35);
        // ob.finalOutput();
        System.out.println(ob.sstList);
        System.out.println(ob.ratesList);
        // System.out.println(ob.userList);
        System.out.println(ob.srpScore);
        System.out.println(ob.userList);
        Set<Integer> inputUsers = new HashSet<>(ob.source);
        System.out.println(inputUsers.size());
        System.out.println(ob.finalOutput);
    }
}