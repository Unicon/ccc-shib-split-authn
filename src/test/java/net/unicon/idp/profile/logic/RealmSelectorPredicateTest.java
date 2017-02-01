package net.unicon.idp.profile.logic;

import static org.testng.Assert.*;

import net.shibboleth.idp.authn.context.SubjectContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.testng.annotations.Test;


/**
 * Created by jgasper on 11/24/15.
 */
public class RealmSelectorPredicateTest {

    @Test
    public void testApplyGood() throws Exception {
        SubjectContext subjectContext = new SubjectContext();
        subjectContext.setPrincipalName("user@good");

        ProfileRequestContext profileRequestContext = new ProfileRequestContext();
        profileRequestContext.addSubcontext(subjectContext);

        RealmSelectorPredicate realmSelectorPredicate = new RealmSelectorPredicate("good");
        assertTrue(realmSelectorPredicate.apply(profileRequestContext));
    }

    @Test
    public void testApplyBad() throws Exception {
        SubjectContext subjectContext = new SubjectContext();
        subjectContext.setPrincipalName("user@bad");

        ProfileRequestContext profileRequestContext = new ProfileRequestContext();
        profileRequestContext.addSubcontext(subjectContext);

        RealmSelectorPredicate realmSelectorPredicate = new RealmSelectorPredicate("good");
        assertFalse(realmSelectorPredicate.apply(profileRequestContext));
    }

    @Test
    public void testApplyNull() throws Exception {
        ProfileRequestContext profileRequestContext = new ProfileRequestContext();

        RealmSelectorPredicate realmSelectorPredicate = new RealmSelectorPredicate("good");
        assertFalse(realmSelectorPredicate.apply(profileRequestContext));
    }

}