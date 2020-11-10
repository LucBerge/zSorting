package fr.zcraft.zsorter.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import fr.zcraft.zlib.components.i18n.I;
import fr.zcraft.zsorter.ZSorterException;
import fr.zcraft.zsorter.tasks.SortTask;

/**
 * The class {@code SorterManager} is used to manage sorters.
 * @author Lucas
 */
public class SorterManager implements Serializable{

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -1782855927147248287L;
	
	private Map<String, Sorter> nameToSorter;
	private transient Map<Inventory, Sorter> inventoryToSorter;
	
	/**
	 * Constructor of a sorter manager object.
	 */
	public SorterManager() {
		this.inventoryToSorter = new HashMap<Inventory, Sorter>();
		this.nameToSorter = new TreeMap<String, Sorter>();
	}
	
	/**
	 * Returns the map linking a name to a sorters.
	 * @return The sorters of the plugin.
	 */
	public Map<String, Sorter> getNameToSorter() {
		return nameToSorter;
	}
	
	/**
	 * Returns the map linking a inventory to a sorter.<br><br>
	 * Do not use this method if you need to add or remove a sorter.
	 * Use the {@code addSorter} and {@code deleteSorter} methods instead.
	 * @return The sorters of the plugin.
	 */
	public Map<Inventory, Sorter> getInventoryToSorter() {
		return inventoryToSorter;
	}
	
	/**
	 * Add a new sorter to the manager.<br><br>
	 * If the sorter already exists, the description is updated.
	 * @param name - Name of the sorter.
	 * @param description - Short description of the sorter.
	 * @return The created sorter object.
	 * @throws ZSorterException if a sorter with this name already exists.
	 */
	public Sorter addSorter(String name, String description) throws ZSorterException {
		Sorter sorter = new Sorter(name, description);										//Create a new sorter
		if(nameToSorter.putIfAbsent(name, sorter) != null)										//If a sorter with this name already exists
			throw new ZSorterException(I.t("A sorter with this name already exists"));
		return sorter;
	}
	
	/**
	 * Remove a sorter from the manager.
	 * @param name - Name of the sorter.
	 * @return The removed sorter object, {@code null} if no sorter has this name.
	 * @throws ZSorterException  if no sorter with this name exists.
	 */
	public Sorter deleteSorter(String name) throws ZSorterException {
		Sorter sorter = nameToSorter.remove(name);										//Get the existing sorter
		if(sorter == null)															//If no sorter has this name
			throw new ZSorterException(I.t("There is no sorter with this name."));		//Display error message
		
		for(Input input:sorter.getInventoryToInput().values())							//For each input of the sorter
			inventoryToSorter.remove(input.getInventory());								//Remove the input from the inventory to sorter map
		for(Input input:sorter.getInventoryToInput().values())							//For each ouput of the sorter
			inventoryToSorter.remove(input.getInventory());								//Remove the output from the inventory to sorter map
		return sorter;
	}
	
	/**
	 * Sets a new sorter input.
	 * @param name - Name of the sorter.
	 * @param inventory - Input inventory.
	 * @param priority - Priority of the input.
	 * @throws ZSorterException if a ZSorter exception occurs.
	 */
	public void setInput(String name, Inventory inventory, int priority) throws ZSorterException {
		Sorter sorter = nameToSorter.get(name);
		if(sorter == null)
			throw new ZSorterException(I.t("There is no sorter with this name."));

		Sorter existingSorter = inventoryToSorter.putIfAbsent(inventory, sorter);											//Get the sorter with this input
		if(existingSorter != null && !sorter.equals(existingSorter))															//If the sorter is not this one
			throw new ZSorterException(I.t("This holder is already in use by the sorter {0}.", sorter.getName()));			//Display error messsage
		
		sorter.setInput(inventory, priority);
	}
	
	/**
	 * Remove an input from a sorter.
	 * @param name - Name of the sorter.
	 * @param inventory - Inventory of the input.
	 * @return The removed input object, {@code null} if no input found for this inventory.
	 * @throws ZSorterException if a ZSorter exception occurs.
	 */
	public Input removeInput(String name, Inventory inventory) throws ZSorterException {
		Sorter sorter = nameToSorter.get(name);
		if(sorter == null)
			throw new ZSorterException(I.t("There is no sorter with this name."));
		
		Input input = sorter.removeInput(inventory);
		if(input == null)
			throw new ZSorterException(I.t("This holder is not an input."));
			
		inventoryToSorter.remove(inventory);			//Unkink the sorter
		return input;
	}
	
	/**
	 * Sets a new sorter output.
	 * @param name - Name of the sorter.
	 * @param inventory - Output inventory.
	 * @param priority - Priority of the output.
	 * @param materials - Materials of the output.
	 * @throws ZSorterException if a ZSorter exception occurs.
	 */
	public void setOutput(String name, Inventory inventory, int priority, List<Material> materials) throws ZSorterException {
		Sorter sorter = nameToSorter.get(name);
		if(sorter == null)
			throw new ZSorterException(I.t("There is no sorter with this name."));

		Sorter existingSorter = inventoryToSorter.putIfAbsent(inventory, sorter);											//Get the sorter with this input
		if(existingSorter != null && !sorter.equals(existingSorter))															//If the sorter is not this one
			throw new ZSorterException(I.t("This holder is already in use by the sorter {0}.", sorter.getName()));			//Display error messsage
		
		sorter.setOutput(inventory, priority, materials);
	}
	
	/**
	 * Remove an output from a sorter.
	 * @param name - Name of the sorter.
	 * @param inventory - Inventory of the output.
	 * @return The removed output object, {@code null} if no output found for this inventory.
	 * @throws ZSorterException if a ZSorter exception occurs.
	 */
	public Output removeOutput(String name, Inventory inventory) throws ZSorterException {
		Sorter sorter = nameToSorter.get(name);
		if(sorter == null)
			throw new ZSorterException(I.t("There is no sorter with this name."));
		
		Output output = sorter.removeOutput(inventory);
		if(output == null)
			throw new ZSorterException(I.t("This holder is not an output."));
		
		inventoryToSorter.remove(inventory);									//Unkink the sorter
		return output;
	}
	
	/**
	 * Returns a list of sorters to compute.
	 * @return List of sorters to compute.
	 */
	public List<Sorter> canCompute(){
		return nameToSorter
				.values()
				.stream()
				.filter(b -> b.isToCompute())
				.collect(Collectors.toList());
	}
	
	/**
	 * Compute the sorter associated with this inventory.
	 * Don't do anything if the inventory is not an input.
	 * @param inventory - Inventory of the sorter to compute.
	 * @return {@code true} if the sorter has been computed, {@code false} otherwise.
	 */
	public boolean computeSorter(Inventory inventory) {
		boolean computed = false;
		Sorter sorter = inventoryToSorter.get(inventory);						//Get the sorter associated with this inventory
		if(sorter != null && sorter.isEnable()) {							//If sorter found and enable
			Input input = sorter.getInventoryToInput().get(inventory);		//Get the input linked to this inventory
			if(input != null) {												//If input found
				sorter.setToCompute(true);										//Set the sorter to compute
				SortTask.getInstance().start();								//Start the task
				computed = true;
			}
			else {
				Output output = sorter.getInventoryToOutput().get(inventory);		//Get the output linked to this inventory
				if(output != null) {											//If output found
					
					boolean clogging = output.getMaterials()
							.stream()
							.filter(sorter.getCloggingUpMaterials()::contains)
							.count() > 0;

					if(clogging || output.isOverflow()) {							//If one of the output material was clogging up the inputs or if it is an overflow
						sorter.setToCompute(true);										//Set the sorter to compute
						SortTask.getInstance().start();								//Start the task
						computed = true;
					}
				}
			}
		}
		return computed;
	}
	


	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException
	{
		ois.defaultReadObject();

		inventoryToSorter = new HashMap<Inventory, Sorter>();
		for(Sorter sorter:nameToSorter.values()) {
			for(Input input:sorter.getInventoryToInput().values()) {
				inventoryToSorter.putIfAbsent(input.getInventory(), sorter);
			}
			for(Output output:sorter.getInventoryToOutput().values()) {
				inventoryToSorter.putIfAbsent(output.getInventory(), sorter);
			}
		}
	}
}