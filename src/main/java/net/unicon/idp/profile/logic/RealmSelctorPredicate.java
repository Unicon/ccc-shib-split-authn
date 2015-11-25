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
        final SubjectContext subjectContext = profileRequestContext.getSubcontext(SubjectContext.class);

        if (subjectContext != null && subjectContext.getPrincipalName() != null && subjectContext.getPrincipalName().endsWith(realm)) {
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
