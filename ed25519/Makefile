# XX Todo: build a cross-platform shared lib here, this only works for linux
CFLAGS= -O2 -g -Wall

.c.o:
	 $(CC) -c $(CFLAGS) -fPIC -o $@ $<

OFILES=\
	src/add_scalar.o \
	src/fe.o \
	src/ge.o \
	src/key_exchange.o \
	src/keypair.o \
	src/sc.o \
	src/seed.o \
	src/sha512.o \
	src/sign.o \
	src/verify.o

libed25519.so: $(OFILES)
	$(CC) -shared -o $@ $^

all: libed25519.so

clean:
	-rm -f $(OFILES) libed25519.so
