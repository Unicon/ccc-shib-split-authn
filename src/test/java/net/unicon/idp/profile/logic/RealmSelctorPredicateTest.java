package net.unicon.idp.profile.logic;

import static org.testng.Assert.*;

import net.shibboleth.idp.authn.context.SubjectContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.testng.annotations.Test;


/**
 * Created by jgasper on 11/24/15.
 */
public class RealmSelctorPredicateTest {

    @Test
    public void testApplyGood() throws Exception {
        SubjectContext subjectContext = new SubjectContext();
        subjectContext.setPrincipalName("user@good");

        ProfileRequestContext profileRequestContext = new ProfileRequestContext();
        profileRequestContext.addSubcontext(subjectContext);

        RealmSelctorPredicate realmSelctorPredicate = new RealmSelctorPredicate("good");
        assertTrue(realmSelctorPredicate.apply(profileRequestContext));
    }

    @Test
    public void testApplyBad() throws Exception {
        SubjectContext subjectContext = new SubjectContext();
        subjectContext.setPrincipalName("user@bad");

        ProfileRequestContext profileRequestContext = new ProfileRequestContext();
        profileRequestContext.addSubcontext(subjectContext);

        RealmSelctorPredicate realmSelctorPredicate = new RealmSelctorPredicate("good");
        assertFalse(realmSelctorPredicate.apply(profileRequestContext));
    }

    @Test
    public void testApplyNull() throws Exception {
        ProfileRequestContext profileRequestContext = new ProfileRequestContext();

        RealmSelctorPredicate realmSelctorPredicate = new RealmSelctorPredicate("good");
        assertFalse(realmSelctorPredicate.apply(profileRequestContext));
    }

}