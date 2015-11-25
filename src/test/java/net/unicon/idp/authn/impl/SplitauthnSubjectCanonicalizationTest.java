package net.unicon.idp.authn.impl;

import static org.testng.Assert.*;

import javax.annotation.meta.When;
import javax.security.auth.Subject;

import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.authn.context.SubjectCanonicalizationContext;
import net.shibboleth.idp.authn.principal.UsernamePrincipal;
import net.shibboleth.idp.profile.RequestContextBuilder;
import net.shibboleth.idp.profile.context.SpringRequestContext;
import net.shibboleth.idp.profile.context.navigate.WebflowRequestContextProfileRequestContextLookup;
import net.shibboleth.idp.profile.ActionTestingSupport;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.opensaml.profile.context.ProfileRequestContext;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockRequestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * Created by jgasper on 11/24/15.
 */
public class SplitauthnSubjectCanonicalizationTest {
    protected RequestContext src;
    protected ProfileRequestContext prc;
    private SplitauthnSubjectCanonicalization action;

    @BeforeMethod public void setUp() throws Exception {
        src = new RequestContextBuilder().buildRequestContext();
        src.getConversationScope().put("realm", "test");
        prc = new WebflowRequestContextProfileRequestContextLookup().apply(src);
        action = new SplitauthnSubjectCanonicalization();
        action.initialize();
    }

    @Test
    public void testDoExecute() throws Exception {

    }


    @Test public void testNoPrincipal() {
        Subject subject = new Subject();
        prc.getSubcontext(SubjectCanonicalizationContext.class, true).setSubject(subject);

        final Event event = action.execute(src);

        ActionTestingSupport.assertEvent(event, AuthnEventIds.INVALID_SUBJECT);
        assertNotNull(prc.getSubcontext(SubjectCanonicalizationContext.class, false).getException());
    }

    @Test public void testMultiPrincipals() {
        Subject subject = new Subject();
        subject.getPrincipals().add(new UsernamePrincipal("foo"));
        subject.getPrincipals().add(new UsernamePrincipal("bar"));
        prc.getSubcontext(SubjectCanonicalizationContext.class, true).setSubject(subject);

        final Event event = action.execute(src);

        ActionTestingSupport.assertEvent(event, AuthnEventIds.INVALID_SUBJECT);
        assertNotNull(prc.getSubcontext(SubjectCanonicalizationContext.class, false).getException());
    }

    @Test public void testSuccess() {
        Subject subject = new Subject();
        subject.getPrincipals().add(new UsernamePrincipal("foo"));
        prc.getSubcontext(SubjectCanonicalizationContext.class, true).setSubject(subject);

        final Event event = action.execute(src);

        ActionTestingSupport.assertProceedEvent(event);
        SubjectCanonicalizationContext sc = prc.getSubcontext(SubjectCanonicalizationContext.class, false);
        assertEquals(sc.getPrincipalName(), "foo@test");
    }
}