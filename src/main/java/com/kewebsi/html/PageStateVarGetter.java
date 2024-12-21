package com.kewebsi.html;

import com.kewebsi.service.PageStateVarColdLink;

@FunctionalInterface
public interface PageStateVarGetter<S> {
	PageStateVarColdLink get(S pageState);
}

