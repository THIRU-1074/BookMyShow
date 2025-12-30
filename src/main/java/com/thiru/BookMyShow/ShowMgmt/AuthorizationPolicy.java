package com.thiru.BookMyShow.ShowMgmt;

public interface AuthorizationPolicy<R, A> {
    void canCreate(A actor);

    void canRead(R resource, A actor);

    void canUpdate(R resource, A actor);

    void canDelete(R resource, A actor);
}
