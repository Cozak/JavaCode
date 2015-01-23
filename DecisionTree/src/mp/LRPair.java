package mp;

/**
 * structure for pair-value
 * @author ZAK
 *
 * @param <L>
 * @param <R>
 */
public class LRPair<L, R> {
	private L left;
	private R right;

	public LRPair(L l, R r) {
		this.left = l;
		this.right = r;
	}

	public L getFirst() {
		return this.left;
	}

	public R getSecond() {
		return this.right;
	}
}
