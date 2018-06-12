package main.java.logic.neural;


import java.util.LinkedList;

public class BasicQueue<E> {

    private LinkedList<E> list = new LinkedList<E>();

    public void put(E o) {
        list.addLast(o);
    }

    public E get() {
        if (list.isEmpty()) {
            return null;
        }
        return list.removeFirst();
    }

    public Object[] getAll() {
        Object[] res = new Object[list.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = list.get(i);
        }
        list.clear();
        return res;
    }

    public E peek() {
        return list.getFirst();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

}