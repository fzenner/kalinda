package com.fzenner.datademo.service;

import com.fzenner.datademo.StaticContextAccessor;
import com.fzenner.datademo.entity.ingredient.IngredientAssistant;
import com.fzenner.datademo.entity.taco.TacoAssistant;
import com.kewebsi.controller.DtoAssistant;
import com.kewebsi.service.DbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fzenner.datademo.entity.ingredient.Ingredient;
import com.fzenner.datademo.entity.taco.Taco;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service encapsulating the repository.
 * @author A4328059
 */

@Service
@Transactional // Because if the refresh() invocation during save.
public class DataDemoService{


	@Autowired
	protected RepositorySafe repositorySafe;

	@Autowired DbService dbService;
	
	// private static Logger LOG = LoggerFactory.getLogger(DataDemoService.class);


	public Taco persistNewTaco(Taco taco) {

		dbService.persistDto(TacoAssistant.getGlobalInstance(), taco);
		//var repository = repositorySafe.getRepositoryFor(Taco.class);
		// Taco savedTaco = repository.save(taco);
		return taco;
	}
	

	public Iterable<Taco> searchTacos() {
		DbService dbService = StaticContextAccessor.getBean(DbService.class);
		return dbService.selectFromDb(TacoAssistant.getGlobalInstance(), null, null);
//		var repository = repositorySafe.getRepositoryFor(Taco.class);
//		Iterable<Taco> tacos = repository.findAll();
//		return tacos;
	}
	
	
	public Iterable<Ingredient> searchIngredient() {
		DbService dbService = StaticContextAccessor.getBean(DbService.class);
		return dbService.selectFromDb(IngredientAssistant.getGlobalInstance(), null, null);
//		var repository = repositorySafe.getRepositoryFor(Ingredient.class);
//		Iterable<Ingredient> ingredients = repository.findAll();
//		return ingredients;
	}
	
	
	public Ingredient persist(Ingredient entity) {
		dbService.persistDto(IngredientAssistant.getGlobalInstance(), entity);
		return entity;
//		var repository = repositorySafe.getRepositoryFor(Ingredient.class);
//		Ingredient savedEntity = repository.save(entity);
//		return savedEntity;
	}
	
	
	public void save(Taco taco) {
		dbService.persistDto(TacoAssistant.getGlobalInstance(), taco);
//		JpaRepository repository = repositorySafe.getRepositoryFor(Taco.class);
//		repository.save(taco);
	}

	// @Transactional
	public <T> T saveEntity(T entity, DtoAssistant<T> dtoAssistant) {
		// DtoAssistant dtoAssistant = StaticContextAccessor.getDtoAssistantFor(entity);
		dbService.persistDto(dtoAssistant, entity);
		return entity;

//		var repository = StaticContextAccessor.getRepository(entity.getClass());
//		return (T) repository.save(entity);

	}


	public RepositorySafe getRepositorySafe() {
		return repositorySafe;
	}
	

	
}
