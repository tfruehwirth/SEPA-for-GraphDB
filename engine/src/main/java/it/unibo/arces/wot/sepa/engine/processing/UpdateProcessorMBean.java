/* JMX class to collect update processing statistics
 * 
 * Author: Luca Roffia (luca.roffia@unibo.it)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package it.unibo.arces.wot.sepa.engine.processing;

public interface UpdateProcessorMBean {
	public void reset();
	
	public long getRequests();
	public long getTimedOutRequests();
	public long getAbortedRequests();
	
	public float getTimingsCurrent();
	public float getTimingsMin();
	public float getTimingsAverage();
	public float getTimingsMax();
	
	public long getTimeout();
	public void setTimeout(long t);
	public int getTimeoutNRetry();
	public void setTimeoutNRetry(int n);
	
	public void scale_ms();
	public void scale_us();
	public void scale_ns();
	public String getUnitScale();
}
