package pe.edu.vallegrande.app.service.impl;

import pe.edu.vallegrande.app.model.Product;
import pe.edu.vallegrande.app.repository.ProductRepository;
import pe.edu.vallegrande.app.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Flux<Product> findAll() {
        log.info("Mostrando productos");
        return productRepository.findAll();
    }

    @Override
    public Mono<Product> findById(Long id) {
        log.info("Mostrando producto por ID: " + id);
        return productRepository.findById(id);
    }

    @Override
    public Mono<Product> save(Product product) {
        log.info("Registrando producto: " + product.toString());
        product.setStatus("A");
        return productRepository.save(product);
    }

    @Override
    public Mono<Product> update(Product product) {
        log.info("Actualizando producto: " + product.toString());
        return productRepository.save(product);
    }

}
