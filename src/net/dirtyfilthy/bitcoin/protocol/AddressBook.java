package net.dirtyfilthy.bitcoin.protocol;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import net.dirtyfilthy.bitcoin.core.Address;

public class AddressBook {
	private Vector<Address> addressList;
	
	public AddressBook(){
		this.addressList=new Vector<Address>();
		
	}
	
	public Vector<Address> getAddressConnectList(){
		Vector<Address> active=getActive();
		Collections.sort(active, new LastTryComparator());
		return active;
	}
	
	public Vector<Address> getActive(){
		Vector<Address> active=new Vector<Address>();
		Calendar c=Calendar.getInstance();
		c.add(Calendar.HOUR,-3);
		java.util.Date threeHoursAgo=c.getTime();
		for(Address a : this.addressList){
			if(a.getLastSeen().compareTo(threeHoursAgo)>0){
				active.add(a);
			}
		}
		return active;
	}
	
	public synchronized void justSeen(Address a){
		Address our=findOrCreateAddress(a);
		our.setLastSeen(new java.util.Date());
	}
	
	public synchronized void justTried(Address a){
		Address our=findOrCreateAddress(a);
		our.setLastTry(new java.util.Date());
	}
	
	public synchronized void justConnected(Address a){
		Address our=findOrCreateAddress(a);
		our.setLastConnected(new java.util.Date());
	}
	
	public synchronized void received(Address a){
		Address ourAddress=findOrCreateAddress(a);
		if(ourAddress.getLastSeen().compareTo(a.getLastSeen())<0){
			ourAddress.setLastSeen(a.getLastSeen());
		}
	}
	
	private Address findOrCreateAddress(Address a){
		Address result;
		int i=addressList.indexOf(a);
		if(i==-1){
			addressList.add(a);
			result=a;
		}
		else{
			result=addressList.get(i);
		}
		return result;
	}
	
	private class LastTryComparator implements Comparator<Address> {

		public int compare(Address a, Address b) {
			return a.getLastTry().compareTo(b.getLastTry())*-1;
		}
	}
	
	
	
	
	
}
