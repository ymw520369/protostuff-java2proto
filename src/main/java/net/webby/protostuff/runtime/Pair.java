package net.webby.protostuff.runtime;

/**
 * 
 * @author Alex Shvid
 *
 */


public final class Pair<F, S> {

	private final F first;
	private final S second;
	
	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}

	public static <F, S> Pair<F, S> newPair(F first, S second) {
		return new Pair<F, S>(first, second);
	}
	
	public F getFirst() {
		return first;
	}

	public S getSecond() {
		return second;
	}

	@Override
	public String toString() {
		return "Pair [first=" + first + ", second=" + second + "]";
	}

}
