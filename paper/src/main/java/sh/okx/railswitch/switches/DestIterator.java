package sh.okx.railswitch.switches;

import java.util.Iterator;

/** An iterator over dests in a space-separated destination string. This supports .remove() */
public class DestIterator implements Iterator<String> {
	private String string;
	/** Returns the destination string, which may have been modified by .remove */
	public String dest_string() { return string; }

	//The [start, end) range of the next substring to be yielded
	private int start = 0;
	private int end;
	private void update_end() {
		end = string.indexOf(" ", start);
		if (end < 0) end = string.length();
	}

	/** Constructs a DestIterator for the given space-separated destination string */
	public DestIterator(String dest_string) {
		string = dest_string;
		update_end();
	}

	//The [start, end) range of current substring staged for removal
	private boolean can_remove = false;
	private int remove_start = -1;
	private int remove_end = -1;

	@Override public boolean hasNext() {
		//This is <= because if the source string ended with a space, the last item to be yielded
		//should be a zero-length string
		return start <= string.length();
	}
	@Override public String next() {
		if (!hasNext()) throw new RuntimeException("Cannot iterate beyond last dest");

		remove_start = start;
		//Add 1 to include the trailing space, but don't overflow beyond the string
		remove_end = Math.min(end + 1, string.length());
		can_remove = true;

		String slice = string.substring(start, end);

		start = end + 1; //Skip over the space
		update_end();
		return slice;
	}

	@Override public void remove() {
		if (!can_remove) throw new RuntimeException("Cannot remove from the iterator currently");
		can_remove = false;
		int length = remove_end - remove_start;
		string = string.substring(0, remove_start) + string.substring(remove_end);
		start -= length;
		end -= length;
	}

	/** A convienience constructor returning an Iterable for use with for-each loops */
	public static Iterable<String> iterate(DestIterator iterator) {
		return new Iterable<String>() {
			private boolean has_iterated = false;
			@Override public Iterator<String> iterator() {
				if (has_iterated) throw new RuntimeException("Cannot iterate DestIterator iterable twice");
				has_iterated = true;
				return iterator;
			}
		};
	}
	/** A convienience constructor returning an Iterable for use with for-each loops */
	public static Iterable<String> iterate(String dest_string) {
		return iterate(new DestIterator(dest_string));
	}
}
