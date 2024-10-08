package assembler;

import memory.Process;
import java.util.Stack;

public class Operations {
    private Stack<String> stack;

    public Operations(Process p) {
        this.stack = p.stack;
    }

    public void push(String val) {
        stack.push(val);
    }

    public String pop() {
        return stack.pop();
    }

    public void add() {
        String a = stack.pop();
        String b = stack.pop();
        stack.push(String.valueOf((Integer.parseInt(a) + Integer.parseInt(b))));
    }

    public void sub() {
        String a = stack.pop();
        String b = stack.pop();
        stack.push(String.valueOf((Integer.parseInt(a) - Integer.parseInt(b))));
    }

    public void mul() {
        String a = stack.pop();
        String b = stack.pop();
        stack.push(String.valueOf((Integer.parseInt(a) * Integer.parseInt(b))));
    }

    public void div() {
        String a = stack.pop();
        String b = stack.pop();
        stack.push(String.valueOf((Integer.parseInt(b) / Integer.parseInt(a))));
    }

    public void inc() {
        String a = stack.pop();
        stack.push(String.valueOf((Integer.parseInt(a) + 1)));
    }

    public void dec() {
        String a = stack.pop();
        stack.push(String.valueOf((Integer.parseInt(a) - 1)));
    }
}
