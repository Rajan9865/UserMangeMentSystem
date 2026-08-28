FROM jenkins/jenkins:lts

USER root

# Install Docker CLI, curl, and kubectl
RUN apt-get update && \
    apt-get install -y docker.io curl && \
    curl -LO "https://dl.k8s.io/release/v1.37.0/bin/linux/amd64/kubectl" && \
    install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl && \
    rm kubectl && \
    rm -rf /var/lib/apt/lists/*

USER jenkins