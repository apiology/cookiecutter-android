# typed: true
# frozen_string_literal: true

require 'overcommit'
require 'overcommit/hook/pre_commit/base'

module Overcommit
  module Hook
    module PreCommit
      # Runs Android Lint (via the Gradle wrapper) against the app module.
      class AndroidLint < Base
        # @return [Symbol, Array<Symbol, String>]
        def run
          # @type [Overcommit::Subprocess::Result]
          result = execute(command)
          return :pass if result.success?

          [:fail, result.stdout + result.stderr]
        end
      end
    end
  end
end
