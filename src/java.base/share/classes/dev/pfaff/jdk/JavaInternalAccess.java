package dev.pfaff.jdk;

import jdk.internal.access.SharedSecrets;
import jdk.internal.reflect.CallerSensitive;
import jdk.internal.reflect.CallerSensitiveAdapter;
import jdk.internal.reflect.Reflection;
import jdk.internal.vm.annotation.ForceInline;
import jdk.internal.vm.annotation.Stable;

import java.lang.invoke.MethodHandles.Lookup;

/**
 * Provides access to internal OpenJDK APIs.
 */
public final class JavaInternalAccess {
    @Stable
    private static volatile boolean RESTRICTED;

    private static final JavaInternalAccess INSTANCE = new JavaInternalAccess();

    private JavaInternalAccess() {}

    /**
     * @return the instance
     * @throws SecurityException if access has been restricted by a call to {@link #restrictAccess()}
     */
    public static JavaInternalAccess getInstance() {
        if (RESTRICTED) {
            throw new SecurityException("Access is restricted");
        }
        return INSTANCE;
    }

    /**
     * Restricts access to {@link JavaInternalAccess} by blocking future calls to {@link #getInstance()}.
     */
    public void restrictAccess() {
        if (!RESTRICTED) RESTRICTED = true;
    }

    /**
     * Creates a new {@link Lookup lookup object} with trusted access, which
     * reports the specified class as its {@link Lookup#lookupClass() lookupClass}.
     *
     * @return the new {@link Lookup lookup object}
     */
    @CallerSensitive
    @ForceInline // to ensure Reflection.getCallerClass optimization
    public Lookup trustedLookup() {
        final Class<?> c = Reflection.getCallerClass();
        if (c == null) {
            throw new IllegalCallerException("no caller frame");
        }
        return SharedSecrets.getJavaLangInvokeAccess().makeTrustedLookup(c);
    }

    // Caller-sensitive adapter method for reflective invocation
    @CallerSensitiveAdapter
    private Lookup trustedLookup(Class<?> caller) {
        if (caller.getClassLoader() == null) {
            throw new InternalError("calling trustedLookup() reflectively is not supported: "+caller);
        }
        return SharedSecrets.getJavaLangInvokeAccess().makeTrustedLookup(caller);
    }

    /**
     * Updates module m1 to export a package to module m2. If m1 already
     * exports or opens the package to m2, this operation has no effect.
     *
     * @param m1 the module to update
     * @param pkg the package to export or open
     * @param m2 the module export or open to
     */
    public void addExports(Module m1, String pkg, Module m2) {
        SharedSecrets.getJavaLangAccess().addExports(m1, pkg, m2);
    }

    /**
     * Updates module m1 to open a package to module m2. If m1 already opens
     * the package to m2, this operation has no effect. If m1 already exports
     * the package to m2, the export will be promoted to an open.
     *
     * @param m1 the module to update
     * @param pkg the package to export or open
     * @param m2 the module export or open to
     */
    public void addOpens(Module m1, String pkg, Module m2) {
        SharedSecrets.getJavaLangAccess().addOpens(m1, pkg, m2);
    }
}
