package net.unicon.idp.profile.logic;

import net.shibboleth.idp.authn.context.SubjectContext;
import org.opensaml.profile.context.ProfileRequestContext;


/**
 * Created by jgasper on 11/3/15.
 */
public class RealmSelctorPredicate implements com.google.common.base.Predicate<org.opensaml.profile.context.ProfileRequestContext> {
    /**
     *
     */
    private String realm;

    /**
     *
     * @param realm
     */
    public RealmSelctorPredicate(final String realm) {
        this.realm = realm.trim();
    }

    @Override
    public boolean apply(ProfileRequestContext profileRequestContext) {

        SubjectContext subjectContext;

        try {
            subjectContext = (SubjectContext) profileRequestContext.getSubcontext("net.shibboleth.idp.authn.context.SubjectContext");
        } catch (ClassNotFoundException e) {
            return false;
        }

        if (subjectContext.getPrincipalName().endsWith(realm)) {
            return true;
        }

        return false;
    }

    /**
     *
     * @param realm
     */
    public void realm(final String realm){
        this.realm = realm.trim();
    }
}
