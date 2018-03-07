package org.iguana.parsetree;

import org.iguana.grammar.symbol.Symbol;

import java.util.ArrayList;

public abstract class VisitResult {

    public abstract ResultVisitor visitor();
    public abstract VisitResult merge(VisitResult other);

    public static class Single extends VisitResult {

        private Object value;

        public Single(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return value;
        }

        public VisitResult merge(VisitResult other) {
            return other.visitor().visit(this);
        }

        public ResultVisitor visitor() {
            return new SingleVisitor(this);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Single)) return false;
            Single other = (Single) obj;
            return value.equals(other.value);
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }

    public static class List extends VisitResult {

        private java.util.List<Object> values;

        public List(java.util.List<Object> values) {
            this.values = values;
        }

        public java.util.List<Object> getValues() {
            return values;
        }

        public VisitResult merge(VisitResult other) {
            return other.visitor().visit(this);
        }

        @Override
        public ResultVisitor visitor() {
            return new ListVisitor(this);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof List)) return false;
            List other = (List) obj;
            return values.equals(other.values);
        }

        @Override
        public String toString() {
            return values.toString();
        }
    }

    public static class EBNFResult extends VisitResult {

        private final java.util.List<Object> values;
        private final Symbol symbol;

        EBNFResult(java.util.List<Object> values, Symbol symbol) {
            this.values = values;
            this.symbol = symbol;
        }

        public VisitResult merge(VisitResult other) {
            return other.visitor().visit(this);
        }

        @Override
        public ResultVisitor visitor() {
            return new EBNFResultVisitor(this);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof EBNFResult)) return false;
            EBNFResult other = (EBNFResult) obj;
            return values.equals(other.values);
        }

        @Override
        public String toString() {
            return values.toString();
        }
    }

    public static class ListOfList extends VisitResult {

        private final java.util.List<java.util.List<Object>> values;

        public ListOfList(java.util.List<java.util.List<Object>> values) {
            this.values = values;
        }

        @Override
        public ResultVisitor visitor() {
            return new ListOfListVisitor(this);
        }

        @Override
        public VisitResult merge(VisitResult other) {
            return other.visitor().visit(this);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof ListOfList)) return false;
            ListOfList other = (ListOfList) obj;
            return values.equals(other.values);
        }

        @Override
        public String toString() {
            return values.toString();
        }
    }

    interface ResultVisitor {
        VisitResult visit(Single other);
        VisitResult visit(List other);
        VisitResult visit(EBNFResult other);
        VisitResult visit(ListOfList other);
    }

    class SingleVisitor implements ResultVisitor {

        private Single result;

        public SingleVisitor(Single result) {
            this.result = result;
        }

        @Override
        public VisitResult visit(Single other) {
            java.util.List<Object> values = new ArrayList<>();
            values.add(other.value);
            values.add(result.value);
            return new List(values);
        }

        @Override
        public VisitResult visit(List other) {
            java.util.List<Object> values = new ArrayList<>();
            values.addAll(other.values);
            values.add(result.value);
            return new List(values);
        }

        @Override
        public VisitResult visit(EBNFResult other) {
            throw new RuntimeException("Combination is not possible");
        }

        @Override
        public VisitResult visit(ListOfList other) {
            java.util.List<java.util.List<Object>> values = new ArrayList<>();
            for (java.util.List<Object> otherValues : other.values) {
                java.util.List<Object> list = new ArrayList<>();
                list.addAll(otherValues);
                list.add(result.value);
                values.add(list);
            }
            return new ListOfList(values);
        }
    }

    class ListVisitor implements ResultVisitor {

        private List result;

        ListVisitor(List result) {
            this.result = result;
        }

        @Override
        public VisitResult visit(Single other) {
            java.util.List<Object> values = new ArrayList<>();
            values.add(other.value);
            values.addAll(result.values);
            return new List(values);
        }

        @Override
        public VisitResult visit(List other) {
            java.util.List<java.util.List<Object>> values = new ArrayList<>();
            values.add(other.values);
            values.add(result.values);
            return new ListOfList(values);
        }

        @Override
        public VisitResult visit(EBNFResult result) {
            throw new RuntimeException("Combination is not possible");
        }

        @Override
        public VisitResult visit(ListOfList other) {
            java.util.List<java.util.List<Object>> values = new ArrayList<>();
            for (java.util.List<Object> otherValues : other.values) {
                java.util.List<Object> list = new ArrayList<>();
                list.addAll(otherValues);
                list.addAll(result.values);
                values.add(list);
            }
            return new ListOfList(values);
        }
    }

    class EBNFResultVisitor implements ResultVisitor {

        private EBNFResult result;

        EBNFResultVisitor(EBNFResult result) {
            this.result = result;
        }

        @Override
        public VisitResult visit(Single other) {
            throw new RuntimeException("Combination is not possible");
        }

        @Override
        public VisitResult visit(List other) {
            throw new RuntimeException("Combination is not possible");
        }

        @Override
        public VisitResult visit(EBNFResult other) {
            throw new RuntimeException("Combination is not possible");
        }

        @Override
        public VisitResult visit(ListOfList other) {
            throw new RuntimeException("Combination is not possible");
        }
    }

    class ListOfListVisitor implements ResultVisitor {

        private ListOfList result;

        ListOfListVisitor(ListOfList result) {
            this.result = result;
        }

        @Override
        public VisitResult visit(Single other) {
            java.util.List<java.util.List<Object>> values = new ArrayList<>();
            for (java.util.List<Object> thisValues : result.values) {
                java.util.List<Object> list = new ArrayList<>();
                list.add(other.value);
                list.addAll(thisValues);
                values.add(list);
            }
            return new ListOfList(values);
        }

        @Override
        public VisitResult visit(List other) {
            java.util.List<java.util.List<Object>> values = new ArrayList<>();
            for (java.util.List<Object> thisValues : result.values) {
                java.util.List<Object> list = new ArrayList<>();
                list.addAll(other.values);
                list.addAll(thisValues);
                values.add(list);
            }
            return new ListOfList(values);
        }

        @Override
        public VisitResult visit(EBNFResult other) {
            throw new RuntimeException("Combination is not possible");
        }

        @Override
        public VisitResult visit(ListOfList other) {
            java.util.List<java.util.List<Object>> values = new ArrayList<>();
            for (java.util.List<Object> otherValues : other.values) {
                for (java.util.List<Object> thisValues : result.values) {
                    java.util.List<Object> list = new ArrayList<>();
                    list.addAll(otherValues);
                    list.addAll(thisValues);
                    values.add(list);
                }
            }
            return new ListOfList(values);
        }
    }

}
