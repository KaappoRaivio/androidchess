package kaappo.androidchess.askokaappochess;

class mval_combo
{
	movevalue mv = null;
	chessboard cb = null;   // after x moves to be used in valuation
	chessboard cb_base = null;  // base, to be used in draw_comparison
	
	mval_combo(movevalue m, chessboard b, chessboard bb)
	{
		mv = m.copy();
		if (b != null) cb = b.copy();
		if (bb != null) cb_base = bb;
	}
}