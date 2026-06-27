import 'dart:async';

enum GtdCollection {
  inbox,
  actions,
  projects,
  areas,
  contexts,
  waitingFor,
  somedayMaybe,
  reference,
  dashboard,
}

class GtdChange {
  const GtdChange({required this.collections, this.source});

  final Set<GtdCollection> collections;
  final Object? source;

  bool affects(GtdCollection collection) => collections.contains(collection);
}

class GtdChangeBus {
  final _controller = StreamController<GtdChange>.broadcast();

  Stream<GtdChange> get stream => _controller.stream;

  void notify(GtdChange change) {
    if (_controller.isClosed) return;
    _controller.add(change);
  }

  void dispose() {
    _controller.close();
  }
}
