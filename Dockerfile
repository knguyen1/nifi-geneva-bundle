# Use Maven image based on Amazon Corretto 11
FROM maven:3.9.5-amazoncorretto-11

# Define build arguments with default values
ARG USERNAME=user
ARG USERID=1000
# Set GROUPID default to the value of USERID
ARG GROUPID=${USERID}

# Update package list
RUN yum update -y

# Install wget, git, and sudo
RUN yum install -y wget git sudo

# Create a group and user with specified IDs, add to sudoers
RUN groupadd -g ${GROUPID} ${USERNAME} \
    && useradd -m -u ${USERID} -g ${GROUPID} -s /bin/bash ${USERNAME} \
    && echo "${USERNAME} ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/${USERNAME} \
    && chmod 0440 /etc/sudoers.d/${USERNAME}

RUN echo 'export PS1="\[\033[0;32m\]\u@\h:\[\033[0;34m\]\w\[\033[0m\] $ "' >> /home/${USERNAME}/.bashrc
