package org.edumate.kode.Engine.api.scripting;

import java.util.Collection;
import java.util.Set;

/**
 * This interface can be implemented by an arbitrary Java class.
 *
 * This class can also be subclassed by an arbitrary Java class. Kode will
 * treat objects of such classes just like kode script objects.
 *
 * @since 1.2.5
 */
public interface KodeObject {
    /**
     * Call this object as a function. This is equivalent to 'func.apply(thiz, args)'.
     *
     * @param thiz 'this' object to be passed to the function. This may be null.
     * @param args arguments to method
     * @return result of call
     */
    Object call(final Object thiz, final Object... args);

    /**
     * Call this 'constructor' function to create a new object.
     * This is equivalent to 'new Object(arg1, arg2...)'.
     *
     * @param args arguments to method
     * @return result of constructor call
     */
    Object newObject(final Object... args);

    /**
     * Retrieves a named member of this object.
     *
     * @param name of member
     * @return member
     * @throws NullPointerException if name is null
     */
    Object getMember(final String name);

    /**
     * Does this object have a named member?
     *
     * @param name name of member
     * @return true if this object has a member of the given name
     */
    boolean hasMember(final String name);

    /**
     * Set a named member in this object
     *
     * @param name  name of the member
     * @param value value of the member
     * @throws NullPointerException if name is null
     */
    void setMember(final String name, final Object value);

    /**
     * Remove a named member from this object
     *
     * @param name name of the member
     * @throws NullPointerException if name is null
     */
    void removeMember(final String name);

    /**
     * Retrieves an indexed member of this object.
     *
     * @param index index slot to retrieve
     * @return member
     */
    Object getSlot(final int index);

    /**
     * Does this object have an indexed property?
     *
     * @param slot index to check
     * @return true if this object has a slot
     */
    boolean hasSlot(final int slot);

    /**
     * Set an indexed member in this object
     *
     * @param index index of the member slot
     * @param value value of the member
     */
    void setSlot(final int index, final Object value);


    /// property and value iteration

    /**
     * Returns the set of all property names of this object.
     *
     * @return set of property names
     */
    Set<String> keySet();

    /**
     * Returns the set of all property values of this object.
     *
     * @return set of property values.
     */
    Collection<Object> values();


    /// instanceof check

    /**
     * Checking whether the given object is an instance of 'this' object.
     *
     * @param instance instance to check
     * @return true if the given 'instance' is an instance of this 'function' object
     */
    boolean isInstance(final Object instance);

    /**
     * Checking whether this object is an instance of the given 'clazz' object.
     *
     * @param clazz clazz to check
     * @return true if this object is an instance of the given 'clazz'
     */
    boolean isInstanceOf(final Object clazz);

    /**
     * Class property
     *
     * @return Class property value of this object
     */
    String getClassName();

    /**
     * Is this a function object?
     *
     * @return if this mirror wraps a function instance
     */
    boolean isFunction();

    /**
     * Is this an array object?
     *
     * @return if this mirror wraps an array object
     * @deprecated Temporarily Kept for Use
     */
    @Deprecated
    boolean isArray();


}
