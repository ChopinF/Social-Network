package proiectmap.socialmap.utils.paging;

public class Page <E>{
    private Iterable<E> elementsOnPage;
    private int totalNumberOfElements;

    public Page(Iterable<E> elementsOnPage, int totalNumberOfElements){
        this.elementsOnPage = elementsOnPage;
        this.totalNumberOfElements = totalNumberOfElements;
    }

    public Iterable<E> getElementsOnPage(){
        return this.elementsOnPage;
    }

    public int getTotalNumberOfElements(){
        return this.totalNumberOfElements;
    }
}
