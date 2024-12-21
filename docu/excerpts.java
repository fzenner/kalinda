

class AbstractPageVarField {
    public boolean isDisabled() {
        return !pageStateVar.getFieldAssistant().isEditable();
    }
}




public interface PageStateVarIntf<F> {
    public boolean isMeaningless();
}


public abstract class PageStateVarBase<F> implements PageStateVarIntf<F> {}

    @Override
    public boolean isMeaningless() {
        return ! isRelevant();
    }

    @Override
    public boolean isRelevant() {
        if (checkRelevance == null) {
            return true;
        } else {
            return checkRelevance.apply(this);
        }
    }

}


/**
 Two concepts: Field editable and PageVar relevant.

 Link: Standard behaviour: A field is editable, if the PageVar is relevant and the field is generally editable defined
       by its FieldAssistant.


 *
 *
 *
 *
 */