
import java.io.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static ArrayList<String> exp=new ArrayList<>();
    static ArrayList<Terminator> Terminators=new ArrayList<>();
    static Terminator  firstTerminator;
    static Table table;
    static HashSet<Character> nonTerminators=new HashSet<>();
    static Stack<Character> stack=new Stack<>();
    static Stack<Character> buffer=new Stack<>();
    public static void main(String[] args) {

        try {
            readFile();
        } catch (IOException e) {
            System.out.println("file read error");
        }

        setTerminators();
        setNonTerminators();
//        printTerminators();
        table=new Table();
        DealInput();
//        System.out.println(buffer);
        DealPrediction();
    }
    public static void DealPrediction(){
        stack.push('$');
        stack.push(firstTerminator.name);
        int n=1;
        while (stack.peek()!='$'){
            char c=stack.peek();
            if(!isInTerminators(c)){
                if(c==buffer.peek()){
                    System.out.println(n+". pop "+stack.peek());
                    stack.pop();
                    buffer.pop();
                }
                else {
                    log.errorInput();
                    break;
                }
            }
            else {
                if(table.predictTable[table.line.indexOf(c)][table.row.indexOf(buffer.peek())]!=null){
                    stack.pop();
                    System.out.println(n+". "+table.predictTable[table.line.indexOf(c)][table.row.indexOf(buffer.peek())]);
                    if(table.predictTable[table.line.indexOf(c)][table.row.indexOf(buffer.peek())].charAt(2)!='~'){
                        pushStringIntoStack(table.predictTable[table.line.indexOf(c)][table.row.indexOf(buffer.peek())].substring(2));
                    }
                }else {
                    log.errorInput();
                    break;
                }
            }
            n++;
        }
        System.out.println("The analysis is over");
    }
    public static void pushStringIntoStack(String s){
        for (int i=s.length()-1;i>=0;i--)stack.push(s.charAt(i));
    }
    public static void DealInput(){
        System.out.println("please input the string to be analyse");
        Scanner cin=new Scanner(System.in);
        String s=cin.nextLine();
        Pattern pattern=Pattern.compile("\\d+\\.*\\d*");
        Matcher matcher=pattern.matcher(s);
        String n=matcher.replaceAll("n");
        buffer.push('$');
        for(int i=n.length()-1;i>=0;i--)buffer.push(n.charAt(i));
    }
    public static boolean isNum(char c){
        return c>='0'&&c<='9';
    }
    public static void clearCommonFactor(){
        int n;
        while ((n=haveCommonFactor())!=-1){
            Terminator t=Terminators.get(n);
            HashSet<Character> set=new HashSet<>();
            int index1=0,index2=0;
            for (String s:t.exp){
                if(!set.contains(s.charAt(0)))set.add(s.charAt(0));
                else {
                    index2=t.exp.indexOf(s);
                    break;
                }
            }
            for (String s:t.exp){
                if(s.charAt(0)==t.exp.get(index2).charAt(0)){
                    index1=t.exp.indexOf(s);
                    break;
                }
            }
            int len=maxCommonFactorLength(t.exp.get(index1),t.exp.get(index2));
            Terminator newT=new Terminator(getANewChar());
            String a=t.exp.get(index1).substring(len);
            String b=t.exp.get(index2).substring(len);
            if(a.isEmpty())a+='~';
            if(b.isEmpty())b+='~';
            newT.exp.add(a);
            newT.exp.add(b);
            Terminators.add(newT);
            t.exp.add(t.exp.get(index1).substring(0,len)+newT.name);
            t.exp.remove(index1);
            t.exp.remove(index2);

        }
    }
    public static int maxCommonFactorLength(String a,String b){
        int n=Math.min(a.length(),b.length());
        if(a.contains(b)||b.contains(a))return n;
        for(int i=1;i<n+1;i++){
            if(!a.substring(0,i).equals(b.substring(0,i)))return i-1;
        }
        log.errorCommonFactor();
        return -1;
    }
    public  static  int haveCommonFactor(){
        for(Terminator t:Terminators){
            HashSet<Character> set=new HashSet<>();
            for(int i=0;i<t.exp.size();i++){
                if(!set.contains(t.exp.get(i).charAt(0)))set.add(t.exp.get(i).charAt(0));
                else return Terminators.indexOf(t);
            }
        }
        return -1;
    }


    public static void readFile() throws IOException {
        String fileName= "input.txt";
        FileInputStream io = null;
        try {
            io = new FileInputStream(new File(fileName));
        } catch (FileNotFoundException e) {
            System.out.println(fileName+" not found");
        }
        BufferedReader br=new BufferedReader(new InputStreamReader(io));
        String temp;
        while ((temp=br.readLine())!=null){
            exp.add(temp);
        }

        /*exp.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s);
            }
        });*/
    }
    public static void setNonTerminators(){
        for (Terminator t:Terminators){
            for (String s:t.exp){
                for (int i=0;i<s.length();i++)
                {
                    if (!isInTerminators(s.charAt(i))){
                        nonTerminators.add(s.charAt(i));
                        if(s.charAt(i)=='n')
                            i+=2;
                    }
                }
            }
        }
        nonTerminators.remove('~');
        nonTerminators.add('$');
//        System.out.println(nonTerminators);
    }
    public static void setTerminators(){
        for(String e:exp){
            Terminator terminator=new Terminator(e.charAt(0));
            terminator.setExp(e.substring(2));
            Terminators.add(terminator);
        }
        firstTerminator=Terminators.get(0);
//        firstTerminator=new Terminator(getANewChar());
//        firstTerminator.setExp(String.valueOf(Terminators.get(0).name));
//        Terminators.add(firstTerminator);
//        int n;
//        while ((n=isLeftRecursion(Terminators))!=-1){
//            clearLeftRecursion(Terminators.get(n));
//        }
//        clearCommonFactor();
        setFirst();
        setFollow();
    }
    public static int isLeftRecursion(ArrayList<Terminator> Terminators){
        for(Terminator terminator:Terminators){
            if (terminator.isLeftRecursion())return Terminators.indexOf(terminator);
        }
        return -1;
    }
    public static void clearLeftRecursion(Terminator t){
        char x=getANewChar();
        Terminator n=new Terminator(x);
        ArrayList<String> temp=new ArrayList<>();
        for (String s:t.exp){
            if(s.charAt(0)==t.name){
                temp.add(s);
            }
        }
        for (String s:temp){
            t.exp.remove(s);
            t.exp.add(s.substring(1)+x);
            n.exp.add(s.substring(1));
            n.exp.add(s.substring(1)+x);
        }
        Terminators.add(n);
    }
    public static char getANewChar(){
        for(char a = 'A';a<='Z';a++){
            if(!isInTerminators(a))return a;
        }
        log.errorChar();
        return 0;
    }

    public static boolean isInTerminators(char a){
        for (Terminator t:Terminators){
            if (t.isEqual(a))return true;
        }
        return false;
    }
    public static HashSet<Character> getFirst(Terminator t){
        HashSet<Character> temp=new HashSet<>();
        if(!t.First.isEmpty()){
            temp.addAll(t.First);
        }
        else {

            for(String s:t.exp){
                if(isInTerminators(s.charAt(0))){
                    if(getFirst(s.charAt(0)).contains('~')){
                        int i=0;
                        while (i<s.length()){
                            if(!isInTerminators(s.charAt(i))){
                                temp.add(s.charAt(i));
                                break;
                            }
                            else if(!getFirst(s.charAt(i)).contains('~')){
                                temp.addAll(getFirst(s.charAt(i)));
                                break;
                            }
                            else {
                                HashSet<Character> set=new HashSet<>();
                                set=getFirst(s.charAt(i));
                                set.remove('~');
                                temp.addAll(set);
                                i++;
                            }
                        }
                        if(i==s.length()&&getFirst(s.charAt(s.length()-1)).contains('~'))temp.add('~');
                    }else{
                        temp.addAll(getFirst(s.charAt(0)));
                    }
                }
                else temp.add(s.charAt(0));
            }
        }
        return temp;
    }
    public static HashSet<Character> getFirst(char a){
        if((isInTerminators(a))){
            int n=findTerminatorByName(a);
            if(!Terminators.get(n).First.isEmpty())return Terminators.get(n).First;
            else return getFirst(Terminators.get(n));
        }
        return new HashSet<Character>(Arrays.asList(a));
    }
    public static void setFirst(){
        for (Terminator t:Terminators){
            t.First.addAll(getFirst(t));
        }
    }
    public static void printTerminators(){
        for(Terminator t:Terminators){
            t.printInfo();
            System.out.println("-------------------------------------------");
        }
    }
    public static void setFollow(){
        firstTerminator.Follow.add('$');
       for (Terminator t:Terminators){
           for (String s: t.exp){
               for (int i=0;i<s.length()-1;i++){
                    if(isInTerminators(s.charAt(i))){
                        if(isInTerminators(s.charAt(i+1))){
                            Terminators.get(findTerminatorByName(s.charAt(i))).Follow.addAll(Terminators.get(findTerminatorByName(s.charAt(i+1))).First);
                        }
                        else{
                            Terminators.get(findTerminatorByName(s.charAt(i))).Follow.add(s.charAt(i+1));
                        }
                    }
                    else if(s.charAt(i)=='n')i+=2;
               }
           }
       }
       while (true){
           int flag=0;
           for (Terminator t:Terminators){
               for (String s:t.exp){
                   if(isInTerminators(s.charAt(s.length()-1))){
                       int oldSize=Terminators.get(findTerminatorByName(s.charAt(s.length()-1))).Follow.size();
                       Terminators.get(findTerminatorByName(s.charAt(s.length()-1))).Follow.addAll(t.Follow);
                       int newSize=Terminators.get(findTerminatorByName(s.charAt(s.length()-1))).Follow.size();
                       if(oldSize!=newSize)flag=1;
                       if(Terminators.get(findTerminatorByName(s.charAt(s.length()-1))).First.contains('~')){
                           Terminators.get(findTerminatorByName(s.charAt(s.length()-2))).Follow.addAll(t.Follow);
                       }
                   }
               }
           }
           if(flag==0)break;
       }
       for(Terminator t:Terminators){
           t.Follow.remove('~');
       }
    }

    public static int findTerminatorByName(char a){
        for (Terminator t:Terminators){
            if(t.isEqual(a))return Terminators.indexOf(t);
        }
        log.errorNotFound();
        return -1;
    }
}


class Terminator{
    char name;
    HashSet<Character> First=new HashSet<>();
    HashSet<Character> Follow=new HashSet<>();
    ArrayList<String> exp =new ArrayList<>();
    Terminator(char name){
        this.name=name;
    }
    boolean isEqual(Terminator t){
        return this.name==t.name;
    }
    boolean isEqual(char name){
        return this.name==name;
    }

    boolean isLeftRecursion(){
        for (String s : exp) {
            if (this.isEqual(s.charAt(0))) return true;
        }
        return false;
    }
    void setExp(String s){
        String pattern="\\|";
        String[] temp=s.split(pattern);
        for (String e:temp){
            exp.add(e.strip());
        }
    }

    void printInfo(){
        System.out.println("终结符："+this.name);
        System.out.println("相关表达式："+this.exp);
        System.out.println("First集："+this.First);
        System.out.println("Follow集："+this.Follow);
    }
    Terminator cloneTerminator(){
        Terminator terminator=new Terminator(this.name);
        terminator.exp.addAll(this.exp);
        terminator.First.addAll(this.First);
        terminator.Follow.addAll(this.Follow);
        return terminator;
    }

}

class log{
    static void errorChar(){
        System.out.println("找不到可用字符以消除左递归");
    }
    static void errorNotFound(){
        System.out.println("找不到该终结符");
    }
    static void errorCommonFactor(){
        System.out.println("查找最长前缀时出现错误");
    }
    static void errorInput(){
        System.out.println("无法处理输入符号");
    }
}

class Table{
    ArrayList<Character> row=new ArrayList<>();
    ArrayList<Character> line=new ArrayList<>();
    String[][] predictTable;
    Table(){
        this.row.addAll(Main.nonTerminators);
        this.row.add('$');
        for (Terminator t:Main.Terminators){
            this.line.add(t.name);
        }
        predictTable=new String[line.size()][row.size()];
        for(Terminator t:Main.Terminators){
            int i=line.indexOf(t.name);
            for(String s:t.exp){
                HashSet<Character> first=new HashSet<>();
                first.addAll(Main.getFirst(s.charAt(0)));
                if(first.contains('~')){
                    HashSet<Character> follow=new HashSet<>();
                    follow.addAll(t.Follow);
                    for(char c:follow){
                        int j=row.indexOf(c);
                        String temp=t.name+"→~";
                        predictTable[i][j]=temp;
                    }
                    first.remove('~');
                }
                else{
                    for(char c:first){
                        int j=row.indexOf(c);
                        String temp=t.name+"→"+s;
                        predictTable[i][j]=temp;
                    }
                }
            }
        }
    }
    void selfPrint(){
        System.out.println(Arrays.deepToString(predictTable));
    }
}