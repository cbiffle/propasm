// Copyright (C) 2006 Cliff L. Biffle.
// 
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
package propasm;

/**
 * @author cbiffle
 *
 */
public class AssemblerConfig {
  private boolean verboseLabelDisplay = false;
  private boolean generateBootloader = true;

  public boolean isVerboseLabelDisplay() {
    return verboseLabelDisplay;
  }

  public void setVerboseLabelDisplay(boolean verboseLabelDisplay) {
    this.verboseLabelDisplay = verboseLabelDisplay;
  }

  public boolean isGenerateBootloader() {
    return generateBootloader;
  }

  public void setGenerateBootloader(boolean generateBootloader) {
    this.generateBootloader = generateBootloader; 
  }
}
