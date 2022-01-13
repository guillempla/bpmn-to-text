FROM debian:buster-20200908 AS toobig

# Install FreeLing requirements
RUN apt-get update -q -y && \
    apt-get install -y build-essential cmake wget unzip && \
    apt-get install -y libicu-dev libboost-regex-dev libboost-system-dev \
                       libboost-program-options-dev libboost-thread-dev \
                       libboost-filesystem-dev libboost-iostreams-dev \
                       zlib1g-dev locales &&\
    cat /etc/locale.gen | sed 's/# en_US.UTF-8 UTF-8/en_US.UTF-8 UTF-8/' > /tmp/locgen && \
    mv /tmp/locgen /etc/locale.gen && \
    locale-gen

# Download and install FreeLing
COPY FreeLing /tmp/FreeLing
RUN cd /tmp/FreeLing && \
    for LG in data/ca data/es data/en; do \
       rm -rf $LG/alternatives-*.dat $LG/constr_gram-*.dat $LG/treeler/dep $LG/dep_txala $LG/chunker $LG/asr; \
    done && \
    mkdir build && \
    cd build && \
    cmake .. -DLANGUAGES="ca;en;es" -DVARIANTS="" && \
    np=$(( `nproc`*4/5 )) && np=$(( $np<1 ? 1 : $np )) && \
    echo "Compiling freeling with $np cores" && \
    make -j $np && \
    make install  && \
    cd && \
    rm -rf /tmp/FreeLing
    
# Build and install server code
COPY ./src /tmp/src
COPY ./launch.sh /usr/local/bin/
COPY ./launch-server.sh /usr/local/bin/
RUN cd /tmp/src && \
    make FREELINGDIR=/usr/local  && \
    make install && \
    cd && \
    rm -rf /tmp/src

# Uninstall development tools no longer needed
RUN apt-get --purge -y remove build-essential cmake wget unzip && \
    apt-get autoremove -y && \
    apt-get clean -y && \
    rm -rf /var/lib/apt/lists/*

## get rid of intermediate layers

FROM scratch
COPY --from=toobig / /

# install customized data files.
COPY ./data/en/locucions.dat /usr/local/share/freeling/en/locucions.dat 
COPY ./data/en/coref/relaxcor_dep/fex.dat /usr/local/share/freeling/en/coref/relaxcor_dep/fex.dat
COPY ./data/es/locucions.dat /usr/local/share/freeling/es/locucions.dat 
COPY ./data/es/coref/relaxcor_dep/fex.dat /usr/local/share/freeling/es/coref/relaxcor_dep/fex.dat
COPY ./data/ca/coref/relaxcor_dep/fex.dat /usr/local/share/freeling/ca/coref/relaxcor_dep/fex.dat
COPY ./data/common/pred-nom.dat /usr/local/share/freeling/common/pred-nom.dat

WORKDIR /root

EXPOSE 50005
EXPOSE 60006

ENTRYPOINT /usr/local/bin/launch.sh

