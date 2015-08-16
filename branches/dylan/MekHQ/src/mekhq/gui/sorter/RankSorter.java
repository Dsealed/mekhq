package mekhq.gui.sorter;

import java.util.Comparator;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mekhq.MekHQ;
import mekhq.campaign.Campaign;
import mekhq.campaign.personnel.Person;

 /**
     * A comparator for ranks written as strings with "-" sorted to the bottom always
     * @author Jay Lawson
     *
     */
    public class RankSorter implements Comparator<String> {

        private Campaign campaign;
        private Pattern pattern;
        private Matcher matcher;
        public RankSorter(Campaign c) {
            campaign = c;
            c.getRanks();
        }
        
        @Override
        public int compare(String s0, String s1) {
        	// get the numbers associated with each rank string, and compare
        	pattern = Pattern.compile("id=\"([^\"]+)\"");
        	matcher = pattern.matcher(s0);
        	matcher.find();
        	try {
	        	Person p0 = campaign.getPerson(UUID.fromString(matcher.group(1)));
	        	matcher = pattern.matcher(s1);
	        	matcher.find();
	        	Person p1 = campaign.getPerson(UUID.fromString(matcher.group(1)));
	        	// the rank orders match, try comparing the levels
	        	if (p0.getRankNumeric() == p1.getRankNumeric()) {
	        		// the levels match too, try comparing MD rank
	        		if (p0.getRankLevel() == p1.getRankLevel()) {
	        			return ((Comparable<Integer>)p0.getManeiDominiRank()).compareTo(p1.getManeiDominiRank());
	        		}
	            	return ((Comparable<Integer>)p0.getRankLevel()).compareTo(p1.getRankLevel());
	        	}
	            return ((Comparable<Integer>)p0.getRankNumeric()).compareTo(p1.getRankNumeric());
        	} catch (Exception e) {
        		MekHQ.logError("[DEBUG] RankSorter Exception, s0: "+s0+", s1: "+s1);
        		e.printStackTrace();
        		return 0;
        	}
        }
    }