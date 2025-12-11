package com.projetofinal;


public class MeuHashTable<K, V> {

   
    private static class Entry<K, V> {
        K key;
        V value;
        boolean isTombstone;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.isTombstone = false;
        }
    }

    private Entry<K, V>[] table;
    private int capacity;
    private int size;

    @SuppressWarnings("unchecked")
    public MeuHashTable(int capacity) {
        this.capacity = capacity;
        this.table = new Entry[capacity];
        this.size = 0;
    }

    private int hash(K key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Chave não pode ser nula");
        }

        if (size >= capacity * 0.75) {
            System.out.println("Tabela quase cheia! (Idealmente redimensionaria aqui)");
        }

        int index = hash(key);
        int originalIndex = index;

        int firstTombstoneIndex = -1;

        while (table[index] != null) {
            if (!table[index].isTombstone && table[index].key.equals(key)) {
                table[index].value = value;
                return;
            }

            if (table[index].isTombstone && firstTombstoneIndex == -1) {
                firstTombstoneIndex = index;
            }

            index = (index + 1) % capacity;
            if (index == originalIndex) {
                break;
            }
        }

        if (firstTombstoneIndex != -1) {
            table[firstTombstoneIndex] = new Entry<>(key, value);
        } else {
            table[index] = new Entry<>(key, value);
        }

        size++;
    }

    public V get(K key) {
        if (key == null) {
            return null;
        }

        int index = hash(key);
        int originalIndex = index;

        while (table[index] != null) {
            if (!table[index].isTombstone && table[index].key.equals(key)) {
                return table[index].value;
            }

            index = (index + 1) % capacity;
            if (index == originalIndex) {
                break;
            }
        }

        return null;
    }

    public void remove(K key) {
        if (key == null) {
            return;
        }

        int index = hash(key);
        int originalIndex = index;

        while (table[index] != null) {
            if (!table[index].isTombstone && table[index].key.equals(key)) {
                table[index].isTombstone = true; 
                size--;
                System.out.println("Item removido (túmulo criado no índice " + index + ")");
                return;
            }

            index = (index + 1) % capacity;
            if (index == originalIndex) {
                break;
            }
        }
    }

    public int size() {
        return size;
    }

    public void printInventory() {
        System.out.println("--- Inventário (Hash Table) ---");
        for (int i = 0; i < capacity; i++) {
            if (table[i] != null && !table[i].isTombstone) {
                System.out.println("[" + i + "] " + table[i].key + ": " + table[i].value);
            } else if (table[i] != null && table[i].isTombstone) {
                System.out.println("[" + i + "] <TÚMULO>");
            }
        }
    }
}


