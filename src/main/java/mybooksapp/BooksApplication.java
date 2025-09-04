package mybooksapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

// --- Models ---
// The 'Book' class represents a single book entity.
record Book(Long id,String title,String author,int publicationYear){}

// --- Repository ---
// The 'BookRepository' simulates a database using a simple in-memory list.
class BookRepository {
	private final List<Book> books = new ArrayList<>();
	private final AtomicLong counter = new AtomicLong();

	public BookRepository() {
		books.add(new Book(counter.incrementAndGet(), "The Hitchhiker's Guide to the Galaxy", "Douglas Adams", 1979));
		books.add(new Book(counter.incrementAndGet(), "1984", "George Orwell", 1949));
		books.add(new Book(counter.incrementAndGet(), "To Kill a Mockingbird", "Harper Lee", 1960));
	}

	public List<Book> findAll() {
		return books;
	}

	public Optional<Book> findById(Long id) {
		return books.stream().filter(book -> book.id().equals(id)).findFirst();
	}

	public Book save(Book book) {
		if (book.id() == null) {
			Book newBook = new Book(counter.incrementAndGet(), book.title(), book.author(), book.publicationYear());
			books.add(newBook);
			return newBook;
		} else {
			// Simple update logic
			delete(book.id());
			books.add(book);
			return book;
		}
	}

	public void delete(Long id) {
		books.removeIf(book -> book.id().equals(id));
	}
}

// --- Service Layer ---
// The 'BookService' contains the business logic.
class BookService {
	private final BookRepository bookRepository = new BookRepository();

	public List<Book> getAllBooks() {
		return bookRepository.findAll();
	}

	public Optional<Book> getBookById(Long id) {
		return bookRepository.findById(id);
	}

	public Book createBook(Book book) {
		return bookRepository.save(book);
	}

	public Book updateBook(Long id, Book updatedBook) {
		return bookRepository
				.save(new Book(id, updatedBook.title(), updatedBook.author(), updatedBook.publicationYear()));
	}

	public void deleteBook(Long id) {
		bookRepository.delete(id);
	}
}

// --- Controller ---
// The 'BookController' handles HTTP requests.
@RestController
@RequestMapping("/api/books")
class BookController {
	private final BookService bookService = new BookService();

	// GET /api/books
	@GetMapping
	public List<Book> getAllBooks() {
		return bookService.getAllBooks();
	}

	// GET /api/books/{id}
	@GetMapping("/{id}")
	public ResponseEntity<Book> getBookById(@PathVariable Long id) {
		Optional<Book> book = bookService.getBookById(id);
		return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	// POST /api/books
	@PostMapping
	public Book createBook(@RequestBody Book book) {
		return bookService.createBook(book);
	}

	// PUT /api/books/{id}
	@PutMapping("/{id}")
	public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
		if (bookService.getBookById(id).isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		Book result = bookService.updateBook(id, updatedBook);
		return ResponseEntity.ok(result);
	}

	// DELETE /api/books/{id}
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
		if (bookService.getBookById(id).isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		bookService.deleteBook(id);
		return ResponseEntity.noContent().build();
	}
}

@SpringBootApplication
public class BooksApplication {
	public static void main(String[] args) {
		SpringApplication.run(BooksApplication.class, args);
	}
}
