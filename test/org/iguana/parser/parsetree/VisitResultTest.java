package org.iguana.parser.parsetree;

import org.iguana.parsetree.VisitResult;
import org.junit.Test;

import static iguana.utils.collections.CollectionsUtil.*;
import static org.junit.Assert.*;

public class VisitResultTest {

    @Test
    public void testSingleSingle() {
        VisitResult single1 = new VisitResult.Single(1);
        VisitResult single2 = new VisitResult.Single(2);
        assertEquals(new VisitResult.List(list(1, 2)), single1.merge(single2));
    }

    @Test
    public void testListSingle() {
        VisitResult list = new VisitResult.List(list(1, 2, 3));
        VisitResult single = new VisitResult.Single(4);
        assertEquals(new VisitResult.List(list(1, 2, 3, 4)), list.merge(single));
    }

    @Test
    public void testSingleList() {
        VisitResult single = new VisitResult.Single(4);
        VisitResult list = new VisitResult.List(list(1, 2, 3));
        assertEquals(new VisitResult.List(list(4, 1, 2, 3)), single.merge(list));
    }

    @Test
    public void testListList() {
        VisitResult list1 = new VisitResult.List(list(1, 2, 3, 4));
        VisitResult list2 = new VisitResult.List(list(5, 6));
        assertEquals(new VisitResult.ListOfList(list(list(1, 2, 3, 4), list(5, 6))), list1.merge(list2));
    }

    @Test
    public void testListOfListSingle() {
        VisitResult listOfList = new VisitResult.ListOfList(list(list(1, 2, 3), list(4, 5), list(6, 7, 8)));
        VisitResult single = new VisitResult.Single(9);
        assertEquals(new VisitResult.ListOfList(list(list(1, 2, 3, 9), list(4, 5, 9), list(6, 7, 8, 9))), listOfList.merge(single));
    }

    @Test
    public void testSingleListOfList() {
        VisitResult single = new VisitResult.Single(9);
        VisitResult listOfList = new VisitResult.ListOfList(list(list(1, 2, 3), list(4, 5), list(6, 7, 8)));
        assertEquals(new VisitResult.ListOfList(list(list(9, 1, 2, 3), list(9, 4, 5), list(9, 6, 7, 8))), single.merge(listOfList));
    }

    @Test
    public void testListOfListList() {
        VisitResult listOfList = new VisitResult.ListOfList(list(list(1, 2, 3), list(4, 5), list(6, 7, 8)));
        VisitResult list = new VisitResult.List(list(9, 10, 11));
        assertEquals(new VisitResult.ListOfList(list(list(1, 2, 3, 9, 10, 11), list(4, 5, 9, 10, 11), list(6, 7, 8, 9, 10, 11))), listOfList.merge(list));
    }

    @Test
    public void testListListOfList() {
        VisitResult list = new VisitResult.List(list(9, 10, 11));
        VisitResult listOfList = new VisitResult.ListOfList(list(list(1, 2, 3), list(4, 5), list(6, 7, 8)));
        assertEquals(new VisitResult.ListOfList(list(list(9, 10, 11, 1, 2, 3), list(9, 10, 11, 4, 5), list(9, 10, 11, 6, 7, 8))), list.merge(listOfList));
    }

    @Test
    public void testListOfListOfList() {
        VisitResult listOfList1 = new VisitResult.ListOfList(list(list(9, 10, 11), list(12, 13, 14), list(15, 16)));
        VisitResult listOfList2 = new VisitResult.ListOfList(list(list(1, 2, 3), list(4, 5), list(6, 7, 8)));
        assertEquals(new VisitResult.ListOfList(list(
            list(9, 10, 11, 1, 2, 3),
            list(9, 10, 11, 4, 5),
            list(9, 10, 11, 6, 7, 8),
            list(12, 13, 14, 1, 2, 3),
            list(12, 13, 14, 4, 5),
            list(12, 13, 14, 6, 7, 8),
            list(15, 16, 1, 2, 3),
            list(15, 16, 4, 5),
            list(15, 16, 6, 7, 8)
        )), listOfList1.merge(listOfList2));
    }

}
