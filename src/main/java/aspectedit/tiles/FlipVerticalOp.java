
package aspectedit.tiles;

/**
 * This operation mirrors a Tile vertically.
 * @author Mark Barnett
 */
public class FlipVerticalOp implements TileOp {

	public FlipVerticalOp() {
	}
	
    @Override
	public boolean isFilterInPlace() {
		return true;
	}


    @Override
	public Tile filter(Tile input) {
		byte[][] bitplanes = input.getBitplanes();
		byte[][] newbitplanes = new byte[8][4];
		
		for(int y=0; y<8; y++) {
			for(int x=0; x<4; x++) {
				newbitplanes[y][x] = bitplanes[7-y][x];
			}
		}
		
		input.setBitplanes(newbitplanes);

        return input;
	}	
	
}