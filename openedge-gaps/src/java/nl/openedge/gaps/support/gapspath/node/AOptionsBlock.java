/* This file was generated by SableCC (http://www.sablecc.org/). */

package nl.openedge.gaps.support.gapspath.node;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import nl.openedge.gaps.support.gapspath.analysis.Analysis;

public final class AOptionsBlock extends POptionsBlock {

    private POptionsPart _optionsPart_;

    private final LinkedList _optionsPartTail_ = new TypedLinkedList(new OptionsPartTail_Cast());

    public AOptionsBlock() {
    }

    public AOptionsBlock(POptionsPart _optionsPart_, List _optionsPartTail_) {
        setOptionsPart(_optionsPart_);

        {
            this._optionsPartTail_.clear();
            this._optionsPartTail_.addAll(_optionsPartTail_);
        }

    }

    public Object clone() {
        return new AOptionsBlock((POptionsPart) cloneNode(_optionsPart_),
                cloneList(_optionsPartTail_));
    }

    public void apply(Switch sw) {
        ((Analysis) sw).caseAOptionsBlock(this);
    }

    public POptionsPart getOptionsPart() {
        return _optionsPart_;
    }

    public void setOptionsPart(POptionsPart node) {
        if (_optionsPart_ != null) {
            _optionsPart_.parent(null);
        }

        if (node != null) {
            if (node.parent() != null) {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _optionsPart_ = node;
    }

    public LinkedList getOptionsPartTail() {
        return _optionsPartTail_;
    }

    public void setOptionsPartTail(List list) {
        _optionsPartTail_.clear();
        _optionsPartTail_.addAll(list);
    }

    public String toString() {
        return "" + toString(_optionsPart_) + toString(_optionsPartTail_);
    }

    void removeChild(Node child) {
        if (_optionsPart_ == child) {
            _optionsPart_ = null;
            return;
        }

        if (_optionsPartTail_.remove(child)) { return; }

    }

    void replaceChild(Node oldChild, Node newChild) {
        if (_optionsPart_ == oldChild) {
            setOptionsPart((POptionsPart) newChild);
            return;
        }

        for (ListIterator i = _optionsPartTail_.listIterator(); i.hasNext();) {
            if (i.next() == oldChild) {
                if (newChild != null) {
                    i.set(newChild);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

    }

    private class OptionsPartTail_Cast implements Cast {

        public Object cast(Object o) {
            POptionsPartTail node = (POptionsPartTail) o;

            if ((node.parent() != null) && (node.parent() != AOptionsBlock.this)) {
                node.parent().removeChild(node);
            }

            if ((node.parent() == null) || (node.parent() != AOptionsBlock.this)) {
                node.parent(AOptionsBlock.this);
            }

            return node;
        }
    }
}