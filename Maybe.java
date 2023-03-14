import java.util.NoSuchElementException;

public abstract class Maybe<T> {
  
  @SuppressWarnings("unchecked")
  public static <T> Maybe<T> none() {
    //safe to type cast None.NONE as Maybe<T>
    return (Maybe<T>) None.NONE;
  }

  public static <T> Maybe<T> some(T t) {
    return new Some<T>(t);
  }

  protected abstract T get();

  public abstract Maybe<T> filter(BooleanCondition<? super T> bc);

  public abstract <U> Maybe<U> map(Transformer<? super T, ? extends U> trans);

  public abstract <U, S extends U> Maybe<U> flatMap(Transformer<? super T, Maybe<S>> trans);
  
  public abstract T orElse(T t);

  public abstract T orElseGet(Producer<? extends T> prod);

  public abstract void ifPresent(Consumer<? super T> consume);

  public static <T> Maybe<T> of(T t) {
    if (t == null) {
      return none();
    } else {
      return some(t);
    }
  }

  //NONE CLASS
  protected static class None extends Maybe<Object> {

    private static final Maybe<?> NONE = new None();
    
    @Override
    public boolean equals(Object obj) {
      return obj == NONE;
    }

    @Override
    protected Object get() {
      throw new NoSuchElementException();
    }

    @Override
    public String toString() {
      return "[]";
    }

    @Override
    public Maybe<Object> filter(BooleanCondition<Object> bc) {
      return super.none();
    }

    @Override
    public <U> Maybe<U> map(Transformer<Object, ? extends U> trans) {
      return super.none();
    }
    
    @Override
    public <U, S extends U> Maybe<U> flatMap(Transformer<Object, Maybe<S>> trans) {
      return Maybe.<U>none();
    }

    @Override
    public Object orElse(Object x) {
      return x;
    }

    @Override
    public Object orElseGet(Producer<? extends Object> prod) {
      return prod.produce();
    }

    @Override
    public void ifPresent(Consumer<? super Object> consumer) {
      return;
    }

  }

  //SOME CLASS
  protected static class Some<T> extends Maybe<T> {

    private T t;

    public Some(T t) {
      if (t == null) {
        this.t = null;
      } else {
        this.t = t;
      }
    }

    @Override
    protected T get() throws NoSuchElementException {
      return this.t;
    }

    @Override
    public Maybe<T> filter(BooleanCondition<? super T> bc) {
      if (this.t == null) {
        return Maybe.some(null);
      }
      if (bc.test(this.t) && this.t != null) {
        return Maybe.of(this.t);
      } else {
        return Maybe.of(null);
      }
    }

    @Override
    public <U> Maybe<U> map(Transformer<? super T, ? extends U> trans) throws NullPointerException {
      if (this.t == null) {
        throw new NullPointerException();
      } else {
        Maybe<U> next = super.some(trans.transform(this.t));
        return next;
      }
    }

    @Override
    public <U, S extends U> Maybe<U> flatMap(Transformer<? super T, Maybe<S>> trans) {
      Maybe<S> maybe = trans.transform(this.t);
      if (maybe.equals(Maybe.none())) {
        return Maybe.none();
      } else {
        return Maybe.some(maybe.get());
      }
    }

    @Override
    public T orElse(T x) {
      return this.get();
    }

    @Override
    public T orElseGet(Producer<? extends T> prod) {
      return this.get();
    }

    @Override
    public void ifPresent(Consumer<? super T> consumer) {
      consumer.consume(this.get());
    }


    public String nameString() {
      return this.t.toString();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj instanceof Some) {
        @SuppressWarnings("unchecked")
        Some<?> other = (Some<?>) obj;
        if (this.t == other.t) {
          return true;
        }
        if (this.t == null || other.t == null) {
          return false;
        }
        return this.t.equals(other.t);
      }
      return false;
    }

    @Override
    public String toString() {
      if (t == null) {
        return "[null]";
      } else {
        return String.format("[%s]", this.nameString());
      }
    }
  }



}
