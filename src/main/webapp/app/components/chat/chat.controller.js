(function() {
	'use strict';
	angular
	.module('soundxtream3App')
	.controller('ChatCtrl', ChatCtrl);

	ChatCtrl.$inject = ['$scope', 'Pubnub' , '$rootScope', 'Principal', '$stateParams'];

	function ChatCtrl ($scope, Pubnub, $rootScope, Principal, $stateParams) {

		Principal.identity().then(function(account){
			vm.user = account;
		});

		var vm = this;

		vm.messages = [];
    	vm.channel = $stateParams.name;

  		var populate = function(){

		    var defaultMessagesNumber = 20;

		    Pubnub.history({
		     channel: vm.channel,
		     callback: function(m){
		        // Update the timetoken of the first message
		        angular.extend(vm.messages, m[0]);  

		        console.log(m);
		        
		        if(m[0].length < defaultMessagesNumber){
		          vm.messagesAllFetched = true;
		        }

		        $rootScope.$digest()
		        $rootScope.$emit('factory:message:populated')
		        
		     },
		     count: defaultMessagesNumber, 
		     reverse: false
		    });

		};

		var getMessages = function() {

		    if (_.isEmpty(vm.messages))
		      populate();

		    return vm.messages;

  		};
  		getMessages();

    	vm.avatarUrl = function() {
        	return '//robohash.org/' + $rootScope.uuid + '?set=set2&bgset=bg2&size=70x70';
    	};

    	vm.sendMessage = function() {
	       // Don't send an empty message 
	       if (!vm.messageContent ||
	            vm.messageContent === '') {
	            return;
	        }
	        Pubnub.publish({
	            channel: vm.channel,
	            message: {
	                content: vm.messageContent,
	                sender_uuid: vm.user.login,
	                date: new Date()
	            },
	            callback: function(m) {
	                console.log(m);
	            }
	        });
	        // Reset the messageContent input
	        vm.messageContent = '';

	    }

	    Pubnub.subscribe({
	        channel: vm.channel,
	        triggerEvents: ['callback']
	    });

	    $scope.scrollDown = function(time) {
	        var $elem = $('.collection');
	        $('body').animate({
	            scrollTop: $elem.height()
	        }, time);
	    };
		$scope.scrollDown(400);

	    $scope.$on(Pubnub.getMessageEventNameFor(vm.channel), function(ngEvent, m) {
	        $scope.$apply(function() {
	            vm.messages.push(m)
	        });
	        $scope.scrollDown(400);
	    });

	}

})();