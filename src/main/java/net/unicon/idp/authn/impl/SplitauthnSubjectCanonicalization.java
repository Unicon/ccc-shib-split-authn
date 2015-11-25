package net.unicon.idp.authn.impl;

import com.google.common.base.Predicate;
import net.shibboleth.idp.profile.context.SpringRequestContext;
import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.authn.SubjectCanonicalizationException;
import net.shibboleth.idp.authn.context.SubjectCanonicalizationContext;
import net.shibboleth.idp.authn.principal.UsernamePrincipal;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.springframework.webflow.execution.RequestContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 *
 * An action that operates on a {@link SubjectCanonicalizationContext} child of the current
 * {@link ProfileRequestContext}, and transforms the input {@link javax.security.auth.Subject}
 * into a principal name by searching for one and only one {@link UsernamePrincipal} custom principal.
 *
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 * @event {@link AuthnEventIds#INVALID_SUBJECT}
 * @pre <pre>ProfileRequestContext.getSubcontext(SubjectCanonicalizationContext.class, false) != null</pre>
 * @post <pre>SubjectCanonicalizationContext.getPrincipalName() != null
 *  || SubjectCanonicalizationContext.getException() != null</pre>
 */
public class SplitauthnSubjectCanonicalization extends net.shibboleth.idp.authn.AbstractSubjectCanonicalizationAction {

    /** Supplies logic for pre-execute test. */
    @Nonnull private final ActivationCondition embeddedPredicate;

    /** The custom Principal to operate on. */
    @Nullable private UsernamePrincipal usernamePrincipal;

    /** Constructor. */
    public SplitauthnSubjectCanonicalization() {
        embeddedPredicate = new ActivationCondition();
    }

    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext, @Nonnull final SubjectCanonicalizationContext c14nContext) {
        if (embeddedPredicate.apply(profileRequestContext, c14nContext, true)) {
            usernamePrincipal = c14nContext.getSubject().getPrincipals(UsernamePrincipal.class).iterator().next();
            return super.doPreExecute(profileRequestContext, c14nContext);
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final SubjectCanonicalizationContext c14nContext) {

        final SpringRequestContext springContext = profileRequestContext.getSubcontext(SpringRequestContext.class);
        final RequestContext requestContext= springContext.getRequestContext();
        final String realm = (String)requestContext.getConversationScope().get("realm");

        c14nContext.setPrincipalName(usernamePrincipal.getName() + "@" + realm);
    }

    /** A predicate that determines if this action can run or not. */
    public static class ActivationCondition implements Predicate<ProfileRequestContext> {

        /** {@inheritDoc} */
        @Override
        public boolean apply(@Nullable final ProfileRequestContext input) {

            if (input != null) {
                final SubjectCanonicalizationContext c14nContext =
                        input.getSubcontext(SubjectCanonicalizationContext.class, false);
                if (c14nContext != null) {
                    return apply(input, c14nContext, false);
                }
            }

            return false;
        }

        /**
         * Helper method that runs either as part of the {@link Predicate} or directly from
         * the {@link SplitauthnSubjectCanonicalization#doPreExecute(ProfileRequestContext, SubjectCanonicalizationContext)}
         * method above.
         *
         * @param profileRequestContext the current profile request context
         * @param c14nContext   the current c14n context
         * @param duringAction  true iff the method is run from the action above
         * @return true iff the action can operate successfully on the candidate contexts
         */
        public boolean apply(@Nonnull final ProfileRequestContext profileRequestContext,
                             @Nonnull final SubjectCanonicalizationContext c14nContext, final boolean duringAction) {

            final Set<UsernamePrincipal> usernames;
            if (c14nContext.getSubject() != null) {
                usernames = c14nContext.getSubject().getPrincipals(UsernamePrincipal.class);
            } else {
                usernames = null;
            }

            if (duringAction) {
                if (usernames == null || usernames.isEmpty()) {
                    c14nContext.setException(
                            new SubjectCanonicalizationException("No UsernamePrincipals were found"));
                    ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.INVALID_SUBJECT);
                    return false;
                } else if (usernames.size() > 1) {
                    c14nContext.setException(
                            new SubjectCanonicalizationException("Multiple UsernamePrincipals were found"));
                    ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.INVALID_SUBJECT);
                    return false;
                }

                return true;
            } else {
                return usernames != null && usernames.size() == 1;
            }
        }

    }
}
